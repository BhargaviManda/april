package com.thbs.BatchManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.thbs.BatchManagement.entity.Batch;
import com.thbs.BatchManagement.entity.EmployeeDTO;
import com.thbs.BatchManagement.exceptionhandler.BatchNotFoundException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateEmployeeException;
import com.thbs.BatchManagement.repository.BatchRepository;
import com.thbs.BatchManagement.service.BatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@CrossOrigin("*")
@RequestMapping("/batch")
public class BatchController {

	
    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchRepository batchRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    
    // merged employees details
    @GetMapping("/merged-employee-details")
    public List<Map<String, Object>> getMergedEmployeeIds() {
        return batchService.fetchMergedEmployeeIds();
    }

    
//    @GetMapping("/merged/employee-ids")
//    public List<Integer> getMergedEmployeesId() {
//        List<Integer> mergedEmployeeId = new ArrayList<>();
//        // Fetch data from the first path
//        String firstPathUrl = "http://172.18.4.185:7001/user";
//        List<Integer> firstPathEmployeeIds = restTemplate.getForObject(firstPathUrl, List.class);
//        mergedEmployeeId.addAll(firstPathEmployeeIds);
//
//        // Fetch data from the second path
//        String secondPathUrl = "http://172.18.4.192:4321/api/v1/users/employeeIds"; 
//        List<Integer> secondPathEmployeeIds = restTemplate.getForObject(secondPathUrl, List.class);
//        mergedEmployeeId.addAll(secondPathEmployeeIds);
//
//        return mergedEmployeeId;
//    }
    
    
    // adding trainees with batch creation
    @PostMapping
    public ResponseEntity<String> createBatch(@RequestBody Batch batch) {
        return batchService.createBatch(batch);
    }

    
    // bulk upload with batch creation
    @PostMapping("/new-batch/bulk")
    public String bulkUpload(@RequestParam("file") MultipartFile file, @RequestParam("data") String data) throws IOException, ParseException {
        List<EmployeeDTO> Employees = batchService.parseExcel(file);
        batchService.addEmployeesToBatchFromExcel(Employees, data);
        return "Batch created successfully";
    }

    
    // adding employees to existing batch by batchid
    @PostMapping("/employee/batch-id/{batchId}")
    public String addEmployeesToBatch(@PathVariable Long batchId, @RequestBody List<EmployeeDTO> employees) {
        batchService.addEmployeesToExistingBatches(batchId, employees);
        return "Employees added to batch successfully";
    }

    
    // adding employees to existing batch by batchname
    @PostMapping("/employee/batch-name/{batchName}")
    public String addEmployeesToBatch(@PathVariable String batchName, @RequestBody List<EmployeeDTO> employees) {
            batchService.addEmployeesToExistingBatch(batchName, employees);
            return "Employees added to batch successfully";  
    }
    
    
    // bulk upload to existing batch by batchid
    @PostMapping("/existing-batch/bulk/batch-id/{batchId}")
    public String addEmployeesToExistingBatchBulkUpload(@PathVariable("batchId") Long batchId, @RequestParam("file") MultipartFile file) throws BatchNotFoundException, DuplicateEmployeeException, IOException {
        List<EmployeeDTO> employees = batchService.parseExcel(file);
        batchService.addEmployeesToExistingBatchesFromExcel(batchId, employees);
        return "Employees added to batch successfully";  
    }

    
    // bulk upload to existing batch by batchname
    @PostMapping("/existing-batch/bulk/batch-name/{batchName}")
    public String addEmployeesToExistingBatchBulkUpload(@PathVariable("batchName") String batchName, @RequestParam("file") MultipartFile file) throws BatchNotFoundException, DuplicateEmployeeException, IOException {
            List<EmployeeDTO> employees = batchService.parseExcel(file);
            batchService.addEmployeesToExistingBatchFromExcel(batchName, employees);
            return "Employees added to batch successfully";   
    }


    // list of batch details by batchid
    @GetMapping("/id/{batchId}")
    public ResponseEntity<Object> getBatchById(@PathVariable Long batchId) {    
    	return batchService.getBatchById(batchId);
    }

    
    // list of batch details by batchname
    @GetMapping("/name/{batchName}")
    public ResponseEntity<Object> getBatchByName(@PathVariable String batchName) {
    	return batchService.getBatchByName(batchName);
    }
    

    // list of all batch details
    @GetMapping
    public List<Batch> getAllBatches() {
        return batchService.getAllBatches();
    }
    
    
    // list of batchnames
    @GetMapping("/name")
    public List<String> getAllBatchNames() {
        return batchService.getAllBatchNames();
    }

    
    // list of employees using batchid
    @GetMapping("/batch-id/employees/{batchId}")
    public List<Long> getEmployeesInBatch(@PathVariable Long batchId) {
        return batchService.getEmployeesInBatch(batchId);
    }
    

    // list of employees using batchname
    @GetMapping("/batch-name/employees/{batchName}")
    public List<Long> getEmployeesInBatchByName(@PathVariable String batchName) {
        return batchService.getEmployeesInBatchByName(batchName);
    }

    
    // list of all batch names along with ids
    @GetMapping("/name/id")
    public List<Map<String, Object>> getAllBatchNamesWithIds() {
        return batchService.getAllBatchNamesWithIds();
    }
    
    
    // list of all employee-details in batch by batchid
    @GetMapping("/employee-details/{batchId}")
    public List<Map<String, Object>> getEmployeesInBatchWithDetails(@PathVariable Long batchId) {
        return batchService.getEmployeesInBatchWithDetails(batchId);
    }
    
    
    // deleting batch with batchid
    @DeleteMapping("/batch-id/{batchId}")
    public String deleteBatch(@PathVariable Long batchId) {
        batchService.deleteBatchById(batchId);
        return "Batch deleted successfully";
    }

    
    // deleting batch with batchname
    @DeleteMapping("/batch-name/{batchName}")
    public String deleteBatch(@PathVariable String batchName) {
        batchService.deleteBatchByName(batchName);
        return "Batch deleted successfully";
    }

    
    // deleting employees with batchname
    @DeleteMapping("/batch-name/{batchName}/{employeeId}")
    public String deleteEmployeeFromBatch(@PathVariable String batchName, @PathVariable int employeeId) {
        batchService.deleteEmployeeFromBatch(batchName, employeeId);
        return "Employee deleted from batch successfully";
    }

     
    // deleting employees with batchid
    @DeleteMapping("/batch-id/{batchId}/{employeeId}")
    public String deleteEmployeeFromBatch(@PathVariable Long batchId, @PathVariable int employeeId) {
        batchService.deleteEmployeeFromBatch(batchId, employeeId);
        return "Employee deleted from batch successfully";
    }
    
    
    //updating enddate with id
    @PatchMapping("/end-date/{batchId}")
    public String updateEndDate(@PathVariable Long batchId, @RequestBody Batch batch) {
        batchService.updateEndDate(batchId, batch.getEndDate());
        return "EndDate updated successfully";
    }
   
    
    //renaming batchname with id
    @PatchMapping("/batch-name/{batchId}")
    public String updateBatchName(@PathVariable Long batchId, @RequestBody Batch batch) {
        batchService.updateBatchName(batchId, batch.getBatchName());
        return "BatchName updated successfully";
    }
    
    
    // edit batch details
    @PutMapping("/{batchId}")
    public String updateBatch(@PathVariable Long batchId, @RequestBody Batch batch) {
        batchService.updateBatch(batchId, batch);
        return "Batch details updated successfully";
    }
    
    
    // finding remaining employees
//    @GetMapping("/remaining-employeesids/{batchName}")
//	public List<Integer> findRemainingEmployee(@PathVariable String batchName) {
//	    List<Integer> allEmployeeIds = getMergedEmployeesId(); // Fetch all employee IDs, you can implement this logic
//	    System.out.println(allEmployeeIds);
//	    return batchService.findRemainingEmployeesIds(batchName, allEmployeeIds);
//	}
    
    
    // finding remaining employees by batchname
    @GetMapping("/remaining-employees/batch-name/{batchName}")
    public List<Map<String, Object>> findRemainingEmployees(@PathVariable String batchName) {
        List<Integer> allEmployeeIds = getMergedEmployeeIds().stream()
                .map(employee -> (Integer) employee.get("empId"))
                .collect(Collectors.toList());

        return batchService.findRemainingEmployees(batchName, allEmployeeIds);
    }
    
    
    // finding remaining employees by batchid
    @GetMapping("/remaining-employees/batch-id/{batchId}")
    public List<Map<String, Object>> findRemainingEmployees(@PathVariable Long batchId) {
        List<Integer> allEmployeeIds = getMergedEmployeeIds().stream()
                .map(employee -> (Integer) employee.get("empId"))
                .collect(Collectors.toList());
        return batchService.findRemainingEmployees(batchId, allEmployeeIds);
    }
    
    
}
