package com.solverminds.booking.service;

import com.solverminds.booking.dto.DocumentListResponse;
import com.solverminds.booking.dto.DocumentUploadResponse;
import com.solverminds.booking.repo.EcomBookingRepository;
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
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int MAX_FILES_PER_REQUEST = 5;
    private static final int MIN_FILES_PER_REQUEST = 1;

    // Document Upload Error Codes (from ErrorCodes class)
    public static final int DOC_BOOKING_NOT_FOUND = 4401;
    public static final int DOC_FOLDER_NOT_FOUND = 4402;
    public static final int DOC_MIN_FILE_COUNT = 4403;
    public static final int DOC_MAX_FILE_COUNT = 4404;
    public static final int DOC_FILE_SIZE_EXCEEDED = 4405;
    public static final int DOC_FILE_NAME_EMPTY = 4406;
    public static final int DOC_FILE_NAME_DUPLICATE = 4407;
    public static final int DOC_FILE_EXTENSION_FORBIDDEN = 4408;
    public static final int DOC_MIME_TYPE_FORBIDDEN = 4409;
    public static final int DOC_FILE_EXISTS_NO_OVERWRITE = 4410;
    public static final int DOC_SETTING_NOT_FOUND = 4411;
    public static final int DOC_BASE_PATH_EMPTY = 4412;

    private static final String MSG_BOOKING_NOT_FOUND = "Booking not found.";

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

    private final EcomBookingRepository bookingRepository;
    private final JdbcTemplate reportJdbcTemplate;
    private final JdbcTemplate ecomJdbcTemplate;

    public DocumentService(EcomBookingRepository bookingRepository,
                          @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate,
                          @Qualifier("ecomJdbcTemplate") JdbcTemplate ecomJdbcTemplate) {
        this.bookingRepository = bookingRepository;
        this.reportJdbcTemplate = reportJdbcTemplate;
        this.ecomJdbcTemplate = ecomJdbcTemplate;
    }

    /**
     * Validate booking for document APIs (V1/V2 list, upload, download).
     * Ensures customer has AP or BP on the booking.
     * First checks ecombooking+ecombooking_cust (ECOM), then booking+booking_cust (ReportDB) if not found.
     * @throws IllegalArgumentException if booking not found or customer has no access
     */
    public void validateBookingForV2Documents(String bookingId, String customerCode) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID is required");
        }
        if (customerCode == null || customerCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer code is required");
        }
        String bid = bookingId.trim();
        String cust = customerCode.trim();

        // First check: ecombooking + ecombooking_cust (ECOM DB)
        final String sqlEcom = "SELECT TOP 1 1 FROM ecombooking e WITH (NOLOCK) " +
                "INNER JOIN ecombooking_cust ec WITH (NOLOCK) ON ec.ebookno = e.ebookno " +
                "WHERE ec.custId = ? AND ec.custTypeId IN ('AP','BP') " +
                "AND (e.ebookno = ? OR e.extBookNo = ?)";
        try {
            Integer found = ecomJdbcTemplate.queryForObject(sqlEcom, Integer.class, cust, bid, bid);
            if (found != null && found == 1) {
                return;
            }
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // Not found in ecombooking, try booking table
        }

        // Second check: booking + booking_cust (ReportDB) - bookingId as book_no
        final String sqlReport = "SELECT TOP 1 1 FROM booking e WITH (NOLOCK) " +
                "INNER JOIN booking_cust ec WITH (NOLOCK) ON ec.book_no = e.book_no " +
                "WHERE ec.custId = ? AND ec.custTypeId IN ('AP','BP') AND e.book_no = ?";
        try {
            Integer found = reportJdbcTemplate.queryForObject(sqlReport, Integer.class, cust, bid);
            if (found != null && found == 1) {
                return;
            }
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // Not found
        }

        throw new IllegalArgumentException("Booking not found with number: " + bookingId);
    }

    /**
     * Upload documents for a booking
     * @param bookingNo The booking number (ebookno)
     * @param customerCode Customer from JWT — must be AP or BP on the booking
     */
    public List<DocumentUploadResponse> uploadDocuments(String bookingNo,
                                                       String customerCode,
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

        // Validate booking exists and customer is AP/BP (same as V2)
        validateBookingForV2Documents(bookingNo, customerCode);

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
     * List documents in a booking folder (V2 API - uses ecombooking/booking validation with customer access check)
     */
    public List<DocumentListResponse> listDocumentsV2(String bookingNo, String customerCode, boolean includeChecksum) throws Exception {
        if (customerCode != null && !customerCode.trim().isEmpty()) {
            validateBookingForV2Documents(bookingNo, customerCode);
        }
        return listDocumentsInternal(bookingNo, includeChecksum);
    }

    /**
     * List documents for a booking (internal use, no validation).
     * Same as documents list endpoint. Returns empty list on error.
     */
    public List<DocumentListResponse> listDocumentsForBooking(String bookingNo) {
        if (bookingNo == null || bookingNo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return listDocumentsInternal(bookingNo.trim(), false);
        } catch (Exception e) {
            logger.debug("Could not list documents for booking {}: {}", bookingNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * List documents in a booking folder
     * @param bookingNo The booking number (ebookno)
     * @param customerCode Customer from JWT — must be AP or BP when present (SYSUSER may be null)
     */
    public List<DocumentListResponse> listDocuments(String bookingNo, String customerCode, boolean includeChecksum) throws Exception {
        if (customerCode != null && !customerCode.trim().isEmpty()) {
            validateBookingForV2Documents(bookingNo, customerCode);
        } else {
            // SYSUSER read-only: existence only (BKON/ecom first; TSHG/ReportDB fallback)
            validateBookingExistsForDocuments(bookingNo);
        }
        return listDocumentsInternal(bookingNo, includeChecksum);
    }

    private List<DocumentListResponse> listDocumentsInternal(String bookingNo, boolean includeChecksum) throws Exception {
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
                        ZoneOffset.UTC
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
     * Download/preview a specific document (V2 API - uses ecombooking/booking validation with customer access check)
     */
    public File downloadDocumentV2(String bookingNo, String fileName, String customerCode) throws Exception {
        if (customerCode != null && !customerCode.trim().isEmpty()) {
            validateBookingForV2Documents(bookingNo, customerCode);
        }
        return downloadDocumentInternal(bookingNo, fileName);
    }

    /**
     * Download/preview a specific document
     * @param bookingNo The booking number (ebookno)
     * @param fileName The file name to download
     * @param customerCode Customer from JWT — must be AP or BP when present (SYSUSER may be null)
     * @return File object if found
     */
    public File downloadDocument(String bookingNo, String fileName, String customerCode) throws Exception {
        if (customerCode != null && !customerCode.trim().isEmpty()) {
            validateBookingForV2Documents(bookingNo, customerCode);
        } else {
            // SYSUSER read-only: existence only (BKON/ecom first; TSHG/ReportDB fallback)
            validateBookingExistsForDocuments(bookingNo);
        }
        return downloadDocumentInternal(bookingNo, fileName);
    }

    /**
     * V1 list/download validation: ecombooking.ebookno first (unchanged BKON path).
     * If not found, accept Documentation DB booking.book_no (any status).
     */
    private void validateBookingExistsForDocuments(String bookingNo) {
        String id = bookingNo != null ? bookingNo.trim() : "";
        if (id.isEmpty()) {
            throw new IllegalArgumentException(MSG_BOOKING_NOT_FOUND);
        }
        if (bookingRepository.findById(id).isPresent()) {
            return;
        }
        if (findReportBookNo(id).isPresent()) {
            return;
        }
        throw new IllegalArgumentException(MSG_BOOKING_NOT_FOUND);
    }

    /** SELECT book_no FROM booking WITH (NOLOCK) WHERE book_no = ? */
    private Optional<String> findReportBookNo(String bookNo) {
        final String sql = "SELECT TOP 1 book_no FROM booking WITH (NOLOCK) WHERE book_no = ?";
        try {
            String found = reportJdbcTemplate.queryForObject(sql, String.class, bookNo);
            return Optional.ofNullable(found);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private File downloadDocumentInternal(String bookingNo, String fileName) throws Exception {
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
     * Then adds: /Ecom/<bookingNo>/ if bookingId starts with BKON, else /BKG/<bookingNo>/
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

            // Use Ecom folder for BKON* (booking request), BKG folder for others (e.g. confirmed booking)
            String subFolder = (bookingNo != null && bookingNo.toUpperCase().startsWith("BKON")) ? "Ecom" : "BKG";
            Path fullPath = Paths.get(basePath, subFolder, bookingNo);

            System.out.println("Resolved booking folder: " + fullPath);
            logger.debug("Resolved booking folder: {} (OS: {}, Base path: {}, subFolder: {})", 
                fullPath, osName, basePath, subFolder);
            
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
     * Delete a specific document from a booking folder.
     * Used by V2 API - V1 does not support delete.
     *
     * @param bookingNo The booking number (ebookno)
     * @param fileName  The file name to delete
     */
    public void deleteDocument(String bookingNo, String fileName) throws Exception {
        bookingRepository.findById(bookingNo)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with number: " + bookingNo));

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }

        Path targetFolder = resolveBookingFolder(bookingNo);
        Path filePath = targetFolder.resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("Not a regular file: " + fileName);
        }

        try {
            Files.delete(filePath);
            logger.info("Deleted document: {} from booking: {}", fileName, bookingNo);
        } catch (java.nio.file.FileSystemException e) {
            throw new IllegalStateException("Document cannot be deleted (in use by another process): " + fileName, e);
        }
    }

    /**
     * Upload documents for a booking (V2 behaviour - no overwrite, conflict on duplicate).
     * @param bookingNo The booking number (ebookno)
     */
    public void uploadDocumentsV2(String bookingNo, List<MultipartFile> files) throws Exception {
        if (files == null || files.size() < MIN_FILES_PER_REQUEST) {
            throw new IllegalArgumentException("At least " + MIN_FILES_PER_REQUEST + " file is required");
        }
        if (files.size() > MAX_FILES_PER_REQUEST) {
            throw new IllegalArgumentException("Maximum " + MAX_FILES_PER_REQUEST + " files allowed per request");
        }

        Set<String> fileNames = new HashSet<>();
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("File name cannot be empty");
            }
            if (!fileNames.add(name)) {
                throw new IllegalArgumentException("Duplicate file name in request: " + name);
            }
        }

        bookingRepository.findById(bookingNo)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with number: " + bookingNo));

        Path targetFolder = resolveBookingFolder(bookingNo);
        if (!Files.exists(targetFolder)) {
            try {
                Files.createDirectories(targetFolder);
                logger.info("Created booking folder: {}", targetFolder);
            } catch (IOException e) {
                logger.error("Failed to create booking folder: {}", targetFolder, e);
                throw new IllegalStateException("Unable to create booking folder: " + targetFolder + ". Error: " + e.getMessage());
            }
        }

        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds maximum limit of 10MB: " + name);
            }

            String extension = getFileExtension(name).toLowerCase();
            if (FORBIDDEN_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException("File type not allowed: " + extension);
            }

            String mimeType = file.getContentType();
            if (mimeType != null && FORBIDDEN_MIME_TYPES.contains(mimeType.toLowerCase())) {
                throw new IllegalArgumentException("MIME type not allowed: " + mimeType);
            }

            Path targetPath = targetFolder.resolve(name);
            if (Files.exists(targetPath)) {
                throw new IllegalStateException("File with identical name already exists: " + name);
            }

            file.transferTo(targetPath.toFile());
            logger.info("File saved (V2): {} (size: {} bytes)", targetPath, file.getSize());
        }
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

