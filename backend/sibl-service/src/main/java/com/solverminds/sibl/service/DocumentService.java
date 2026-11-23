package com.solverminds.sibl.service;

import com.solverminds.sibl.dto.DocumentListResponse;
import com.solverminds.sibl.dto.DocumentUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("siblDocumentService")
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int MAX_FILES_PER_REQUEST = 5;
    private static final int MIN_FILES_PER_REQUEST = 1;

    // Forbidden file extensions
    private static final Set<String> FORBIDDEN_EXTENSIONS = new HashSet<>(Arrays.asList(
        "exe", "jar", "bat", "dat", "sh", "cmd", "msi",
        "mp3", "wav", "aac", "wma", "flac",
        "mp4", "avi", "mkv", "mov", "wmv", "3gp"
    ));

    // Forbidden MIME types
    private static final Set<String> FORBIDDEN_MIME_TYPES = new HashSet<>(Arrays.asList(
        "application/x-msdownload", "application/x-msdos-program", "application/java-archive",
        "application/x-bat", "application/x-sh", "application/x-msi",
        "audio/mpeg", "audio/wav", "audio/x-wav", "audio/aac", "audio/x-ms-wma", "audio/flac",
        "video/mp4", "video/x-msvideo", "video/x-matroska", "video/quicktime", "video/x-ms-wmv", "video/3gpp"
    ));

    private final JdbcTemplate reportJdbcTemplate;

    public DocumentService(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Upload documents for a booking
     * @param bookingNo The booking number (ebookno)
     */
    public List<DocumentUploadResponse> uploadDocuments(String bookingNo, 
                                                       List<MultipartFile> files, 
                                                       boolean overwrite) throws Exception {
        
        // Validate file count
        if (files == null || files.size() < MIN_FILES_PER_REQUEST) {
            throw new IllegalArgumentException("At least " + MIN_FILES_PER_REQUEST + " file is required");
        }
        if (files.size() > MAX_FILES_PER_REQUEST) {
            throw new IllegalArgumentException("Maximum " + MAX_FILES_PER_REQUEST + " files allowed per request");
        }

        // Check for duplicate file names in request
        Set<String> fileNames = new HashSet<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("File name cannot be empty");
            }
            if (!fileNames.add(fileName)) {
                throw new IllegalArgumentException("Duplicate file name in request: " + fileName);
            }
        }

        // Validate booking exists in report database
        validateBookingExists(bookingNo);
        
        // Use the booking number to resolve folder path
        Path targetFolder = resolveBookingFolder(bookingNo);
        
        // Create booking folder if it doesn't exist (Ecom folder should already exist)
        if (!Files.exists(targetFolder)) {
            try {
                Files.createDirectories(targetFolder);
                logger.info("Created booking folder: {}", targetFolder);
            } catch (IOException e) {
                logger.error("Failed to create booking folder: {}", targetFolder, e);
                throw new IllegalStateException("Unable to create booking folder: " + targetFolder + ". Error: " + e.getMessage());
            }
        }

        List<DocumentUploadResponse> responses = new ArrayList<>();

        // Process each file
        for (MultipartFile file : files) {
            try {
                DocumentUploadResponse response = processFile(file, targetFolder, overwrite);
                responses.add(response);
            } catch (Exception e) {
                logger.error("Error processing file: {}", file.getOriginalFilename(), e);
                responses.add(new DocumentUploadResponse(file.getOriginalFilename(), e.getMessage()));
            }
        }

        return responses;
    }

    /**
     * List documents in a booking folder
     * @param bookingNo The booking number (ebookno)
     */
    public List<DocumentListResponse> listDocuments(String bookingNo, boolean includeChecksum) throws Exception {
        // Validate booking exists in report database
        validateBookingExists(bookingNo);
        
        // Use the booking number to resolve folder path
        Path targetFolder = resolveBookingFolder(bookingNo);
        
        // Check if folder exists (for list operation, don't create - just return empty list)
        if (!Files.exists(targetFolder)) {
            logger.info("Booking folder does not exist yet: {}. Returning empty list.", targetFolder);
            return new ArrayList<>(); // Return empty list instead of error
        }

        List<DocumentListResponse> documents = new ArrayList<>();

        try (Stream<Path> paths = Files.list(targetFolder)) {
            List<Path> filesList = paths
                .filter(Files::isRegularFile)
                .filter(p -> !p.getFileName().toString().startsWith(".")) // Exclude hidden files
                .collect(Collectors.toList());

            for (Path filePath : filesList) {
                try {
                    File file = filePath.toFile();
                    String fileName = file.getName();
                    long size = file.length();
                    LocalDateTime lastModified = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(file.lastModified()), 
                        ZoneId.systemDefault()
                    );

                    DocumentListResponse doc = new DocumentListResponse(
                        fileName, size, lastModified
                    );

                    // Calculate checksum if requested
                    if (includeChecksum) {
                        String checksum = calculateChecksum(filePath);
                        doc.setChecksum(checksum);
                    }

                    documents.add(doc);
                } catch (Exception e) {
                    logger.error("Error reading file: {}", filePath, e);
                }
            }
        }

        return documents;
    }

    /**
     * Download/preview a specific document
     * @param bookingNo The booking number (ebookno)
     * @param fileName The file name to download
     * @return File object if found
     */
    public File downloadDocument(String bookingNo, String fileName) throws Exception {
        // Validate booking exists in report database
        validateBookingExists(bookingNo);
        
        // Validate fileName to prevent directory traversal attacks
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        
        // Resolve booking folder
        Path targetFolder = resolveBookingFolder(bookingNo);
        Path filePath = targetFolder.resolve(fileName);
        
        // Check if file exists
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        
        // Check if it's a regular file (not directory)
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("Not a regular file: " + fileName);
        }
        
        logger.info("Serving file: {}", filePath);
        return filePath.toFile();
    }

    /**
     * Validate that booking exists in report database
     */
    private void validateBookingExists(String bookingNo) throws Exception {
        final String sql = "SELECT COUNT(*) FROM ecombooking WITH (NOLOCK) WHERE ebookno = ?";
        try {
            Integer count = reportJdbcTemplate.queryForObject(sql, Integer.class, bookingNo.trim());
            if (count == null || count == 0) {
                throw new IllegalArgumentException("Booking not found with number: " + bookingNo);
            }
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Booking not found with number: " + bookingNo);
        }
    }

    /**
     * Process a single file: validate, calculate checksum, and save
     */
    private DocumentUploadResponse processFile(MultipartFile file, Path targetFolder, boolean overwrite) 
            throws Exception {
        
        String fileName = file.getOriginalFilename();
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB: " + fileName);
        }

        // Validate file type
        String extension = getFileExtension(fileName).toLowerCase();
        if (FORBIDDEN_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type not allowed: " + extension);
        }

        String mimeType = file.getContentType();
        if (mimeType != null && FORBIDDEN_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new IllegalArgumentException("MIME type not allowed: " + mimeType);
        }

        Path targetPath = targetFolder.resolve(fileName);

        // Check if file exists
        if (Files.exists(targetPath)) {
            if (!overwrite) {
                throw new IllegalStateException("File already exists and overwrite=false: " + fileName);
            }
            // Delete existing file if overwrite=true
            Files.delete(targetPath);
            logger.debug("Deleted existing file for overwrite: {}", targetPath);
        }

        // Write directly to target location (no temp file needed)
        file.transferTo(targetPath.toFile());
        
        logger.info("File saved: {} (size: {} bytes)", targetPath, file.getSize());

        return new DocumentUploadResponse(
            fileName, 
            file.getSize()
        );
    }

    /**
     * Resolve booking folder path based on OS and environment
     * Query: select path from emssetting(nolock) where modulename='Bill of Lading' and status='A'
     * Then adds: /Ecom/<bookingNo>/
     */
    private Path resolveBookingFolder(String bookingNo) throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        
        // Query emssetting from ReportDB using native query
        final String sql = "SELECT path FROM emssetting WITH (NOLOCK) WHERE modulename = ? AND status = ?";
        
        try {
            String basePath = reportJdbcTemplate.queryForObject(
                sql, 
                String.class, 
                "Bill of Lading", 
                "A"
            );
            
            if (basePath == null || basePath.trim().isEmpty()) {
                throw new IllegalStateException("Path is empty in emssetting for Bill of Lading module");
            }

            // The path from emssetting differs based on environment (Windows/Linux server)
            // Add "Ecom" folder and then booking number: <basePath>/Ecom/<bookingNo>
            Path fullPath = Paths.get(basePath, "Ecom", bookingNo);
            
            logger.debug("Resolved booking folder: {} (OS: {}, Base path: {})", 
                fullPath, osName, basePath);
            
            return fullPath;
            
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new IllegalStateException(
                "Bill of Lading setting not found in emssetting table (modulename='Bill of Lading', status='A')"
            );
        }
    }

    /**
     * Calculate SHA-256 checksum for a file
     */
    private String calculateChecksum(Path filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
