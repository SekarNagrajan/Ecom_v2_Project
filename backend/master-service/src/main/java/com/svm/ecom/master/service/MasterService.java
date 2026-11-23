package com.svm.ecom.master.service;

import com.svm.ecom.master.model.VesselView;
import com.svm.ecom.master.model.PortView;
import com.svm.ecom.master.model.CountryView;
import com.svm.ecom.master.model.PackageView;
import com.svm.ecom.master.model.EquipmentView;
import com.svm.ecom.master.model.TerminalView;
import com.svm.ecom.master.repository.MasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.svm.ecom.master.model.CommodityView;
import com.svm.ecom.master.model.HSView;
import com.svm.ecom.master.model.DGView;
import java.util.List;

/**
 * Service class for master data operations
 * Provides read-only access to vessel and port master data
 */
@Service
public class MasterService {

    private static final Logger logger = LoggerFactory.getLogger(MasterService.class);

    @Autowired
    private MasterRepository masterRepository;

    /**
     * Retrieve all active vessels
     * @return List of active vessels excluding test vessels
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<VesselView> getActiveVessels() {
        logger.info("Fetching active vessels from database");
        
        List<VesselView> vessels = masterRepository.findActiveVessels();
        
        logger.info("Successfully fetched {} active vessels", vessels.size());
        return vessels;
    }

    /**
     * Retrieve all active ports
     * @return List of active ports excluding test ports
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<PortView> getActivePorts() {
        logger.info("Fetching active ports from database");
        
        List<PortView> ports = masterRepository.findActivePorts();
        
        logger.info("Successfully fetched {} active ports", ports.size());
        return ports;
    }

    /**
     * Retrieve all active countries
     * 
     * @return List of active countries excluding test countries
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<CountryView> getActiveCountries() {
        logger.info("Fetching active countries from database");

        List<CountryView> countries = masterRepository.findActiveCountries();

        logger.info("Successfully fetched {} active countries", countries.size());
        return countries;
    }

    /**
     * Retrieve all active packages
     * 
     * @return List of active packages excluding test packages
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<PackageView> getActivePackages() {
        logger.info("Fetching active packages from database");

        List<PackageView> packages = masterRepository.findActivePackages();

        logger.info("Successfully fetched {} active packages", packages.size());
        return packages;
    }

    /**
     * Retrieve all active equipment
     * 
     * @return List of active equipment excluding test equipment
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<EquipmentView> getActiveEquipment() {
        logger.info("Fetching active equipment from database");

        List<EquipmentView> equipment = masterRepository.findActiveEquipment();

        logger.info("Successfully fetched {} active equipment", equipment.size());
        return equipment;
    }

    /**
     * Retrieve all active terminal
     * 
     * @return List of active terminal excluding test terminal
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<TerminalView> getActiveTerminal() {
        logger.info("Fetching active terminal from database");

        List<TerminalView> terminal = masterRepository.findActiveTerminal();

        logger.info("Successfully fetched {} active terminal", terminal.size());
        return terminal;
    }

    /**
     * Retrieve all active commodity
     * 
     * @return List of active commodity excluding test commodity
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<CommodityView> getActiveCommodity() {
        logger.info("Fetching active commodity from database");

        List<CommodityView> commodity = masterRepository.findActiveCommodity();

        logger.info("Successfully fetched {} active commodity", commodity.size());
        return commodity;
    }

    /**
     * Retrieve all active HS
     * 
     * @return List of active HS excluding test HS
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<HSView> getActiveHS() {
        logger.info("Fetching active HS from database");

        List<HSView> hs = masterRepository.findActiveHS();

        logger.info("Successfully fetched {} active HS", hs.size());
        return hs;
    }

    /**
     * Retrieve all active DG
     * 
     * @return List of active DG excluding test DG
     */
    @Transactional(readOnly = true, transactionManager = "reportTransactionManager")
    public List<DGView> getActiveDG() {
        logger.info("Fetching active DG from database");

        List<DGView> dg = masterRepository.findActiveDG(); 

        logger.info("Successfully fetched {} active DG", dg.size());
        return dg;
    }
}
