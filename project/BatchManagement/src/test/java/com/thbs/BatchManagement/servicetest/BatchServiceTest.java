package com.thbs.BatchManagement.servicetest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.thbs.BatchManagement.entity.Batch;
import com.thbs.BatchManagement.entity.EmployeeDTO;
import com.thbs.BatchManagement.exceptionhandler.BatchEmptyException;
import com.thbs.BatchManagement.exceptionhandler.BatchNotFoundException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateBatchFoundException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateEmployeeException;
import com.thbs.BatchManagement.exceptionhandler.EmployeeNotFoundException;
import com.thbs.BatchManagement.exceptionhandler.EmptyEmployeesListException;
import com.thbs.BatchManagement.exceptionhandler.ParseException;
import com.thbs.BatchManagement.repository.BatchRepository;
import com.thbs.BatchManagement.service.BatchService;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BatchServiceTest {

	@Mock
	private BatchRepository batchRepository;

	@InjectMocks
	private BatchService batchService;

	@Mock
    private Workbook mockWorkbook;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	

	@Test
	public void testCreateBatch() {
		Batch batch = new Batch();
		batch.setBatchName("Test Batch");

		when(batchRepository.existsByBatchName(batch.getBatchName())).thenReturn(false);
		when(batchRepository.save(batch)).thenReturn(batch);

		assertDoesNotThrow(() -> batchService.createBatch(batch));
	}

	
//	@Test
//	public void testParseExcel() throws IOException, InvalidFormatException {
//        // Mock InputStream for the Excel file
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.xlsx");
//        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);
//
//        // Mock WorkbookFactory.create to return a Workbook
//        Workbook mockWorkbook = mock(Workbook.class);
//        when(WorkbookFactory.create(file.getInputStream())).thenReturn(mockWorkbook);
//
//        // Mock behavior for the Workbook
//        Sheet mockSheet = mock(Sheet.class);
//        when(mockWorkbook.getSheetAt(0)).thenReturn(mockSheet);
//
//        // Mock behavior for the Sheet
//        Row mockRow1 = mock(Row.class);
//        Row mockRow2 = mock(Row.class);
//        when(mockSheet.iterator()).thenReturn(List.of(mockRow1, mockRow2).iterator());
//
//        // Mock behavior for the first row
//        Cell mockCell1 = mock(Cell.class);
//        when(mockRow1.getCell(0)).thenReturn(mockCell1);
//        when(mockCell1.getCellType()).thenReturn(CellType.NUMERIC);
//        when(mockCell1.getNumericCellValue()).thenReturn(1.0);
//
//        // Mock behavior for the second row
//        Cell mockCell2 = mock(Cell.class);
//        when(mockRow2.getCell(0)).thenReturn(mockCell2);
//        when(mockCell2.getCellType()).thenReturn(CellType.NUMERIC);
//        when(mockCell2.getNumericCellValue()).thenReturn(2.0);
//        // Create an instance of the service class
//        BatchService batchService = new BatchService();
//
//        // Call the method
//        List<EmployeeDTO> employees = batchService.parseExcel(file);
//
//        // Assert the result
//        assertEquals(2, employees.size());
//        assertEquals(1, employees.get(0).getEmployeeId());
//        assertEquals(2, employees.get(1).getEmployeeId());
//    }
//	
	
    @Test
    public void testAddEmployeesToBatchFromExcel() throws IOException, ParseException, java.text.ParseException {
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1)); // Example employee

        String data = "{\"batchName\": \"Test Batch\", \"duration\": 10, \"startDate\": \"2024-04-08\", \"endDate\": \"2024-04-18\", \"batchSize\": 20}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(data);

        // Mocking the behavior of the batchRepository.existsByBatchName method
        when(batchRepository.existsByBatchName("Test Batch")).thenReturn(false);

        // Call the method to be tested
        batchService.addEmployeesToBatchFromExcel(employees, data);

        // Verify that batchRepository.save was called once
        verify(batchRepository, times(1)).save(any());

        // You can add more assertions based on the expected behavior of the method
    }

    
    @Test
    public void testAddEmployeesToBatchFromExcelDuplicateBatch() throws IOException, ParseException {
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1)); // Example employee

        String data = "{\"batchName\": \"Test Batch\", \"duration\": 10, \"startDate\": \"2024-04-08\", \"endDate\": \"2024-04-18\", \"batchSize\": 20}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(data);

        // Mocking the behavior of the batchRepository.existsByBatchName method to return true (indicating a duplicate batch)
        when(batchRepository.existsByBatchName("Test Batch")).thenReturn(true);

        // Verify that the DuplicateBatchFoundException is thrown
        assertThrows(DuplicateBatchFoundException.class, () ->
        batchService.addEmployeesToBatchFromExcel(employees, data));

        // Verify that batchRepository.save was never called
        verify(batchRepository, never()).save(any());
    }

    
    @Test
    void addEmployeesToExistingBatcheswithValidBatchIdAndEmployeesshouldAddEmployeesToBatch() {
        MockitoAnnotations.openMocks(this); 

        Long batchId = 1L;
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1));

        Batch batch = new Batch();
        batch.setEmployeeId(new ArrayList<>());

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        batchService.addEmployeesToExistingBatches(batchId, employees);

        // Verify that findById was called
        verify(batchRepository, times(1)).findById(batchId);

        // Verify that save was called with the modified batch object
        ArgumentCaptor<Batch> batchCaptor = ArgumentCaptor.forClass(Batch.class);
        verify(batchRepository, times(1)).save(batchCaptor.capture());

        // Check the modified batch object
        Batch modifiedBatch = batchCaptor.getValue();
        assertEquals(employees.size(), modifiedBatch.getEmployeeId().size());
        // Add more assertions as needed for other fields or conditions
    }

    
    @Test
    void addEmployeesToExistingBatcheswithEmptyEmployeesListshouldThrowException() {
        // Arrange
        Long batchId = 1L;
        List<EmployeeDTO> employees = new ArrayList<>();
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(new Batch()));

        // Act and Assert
        assertThrows(EmptyEmployeesListException.class, () -> batchService.addEmployeesToExistingBatches(batchId, employees));

        // Verify
        verify(batchRepository, times(1)).findById(batchId);
        verify(batchRepository, never()).save(any());
    }

    
	@Test
    void addEmployeesToExistingBatcheswithNonExistingBatchIdshouldThrowException() {
        MockitoAnnotations.openMocks(this);

        Long batchId = 1L;
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1L));

        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        assertThrows(BatchNotFoundException.class, () ->
                batchService.addEmployeesToExistingBatches(batchId, employees));

        verify(batchRepository, times(1)).findById(batchId);
        verify(batchRepository,never()).save(any());
    }

	
    @Test
    public void testAddEmployeesToExistingBatch() {
        String batchName = "TestBatch";
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1)); // Example employee

        Batch batch = new Batch();
        batch.setBatchName(batchName);
        batch.setEmployeeId(new ArrayList<>());

        when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));

        assertDoesNotThrow(() -> batchService.addEmployeesToExistingBatch(batchName, employees));

        verify(batchRepository, times(1)).findByBatchName(batchName);
        verify(batchRepository, times(1)).save(batch);
    }

    
    @Test
    public void testAddEmployeesToExistingBatchBatchNotFound() {
        String batchName = "NonExistingBatch";
        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(new EmployeeDTO((long) 1)); // Example employee

        when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.empty());

        assertThrows(BatchNotFoundException.class, () -> batchService.addEmployeesToExistingBatch(batchName, employees));

        verify(batchRepository, times(1)).findByBatchName(batchName);
        verify(batchRepository, never()).save(any());
    }
	    
	    
    @Test
    public void testAddEmployeesToExistingBatchEmptyEmployeesList() {
        String batchName = "TestBatch";
        List<EmployeeDTO> employees = new ArrayList<>();

        Batch batch = new Batch();
        batch.setBatchName(batchName);
        batch.setEmployeeId(new ArrayList<>());

        when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));

        assertThrows(EmptyEmployeesListException.class, () -> batchService.addEmployeesToExistingBatch(batchName, employees));

        verify(batchRepository, never()).save(any());
    }
    
    
    @Test
	public void testAddEmployeesToExistingBatchesEmptyEmployeesList1() {
		Long batchId = 1L;
		List<EmployeeDTO> employees = new ArrayList<>();

		when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

		// Call the method that should throw BatchNotFoundException
		Exception exception = assertThrows(BatchNotFoundException.class,
				() -> batchService.addEmployeesToExistingBatches(batchId, employees));

		assertEquals("Batch not found", exception.getMessage());

		verify(batchRepository, times(1)).findById(batchId);
		verify(batchRepository, never()).save(any());

		// Add another scenario here if needed
	}

 
	@Test
	public void testAddEmployeesToExistingBatchesFromExcelBatchNotFound() {
		Long batchId = 1L;
		List<EmployeeDTO> employees = new ArrayList<>();
		employees.add(new EmployeeDTO((long) 1));
		employees.add(new EmployeeDTO((long) 2));
		employees.add(new EmployeeDTO((long) 3));

		when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

		assertThrows(BatchNotFoundException.class,
				() -> batchService.addEmployeesToExistingBatchesFromExcel(batchId, employees));

		verify(batchRepository, times(1)).findById(batchId);
		verify(batchRepository, never()).save(any());
	}

	
	@Test
	public void testAddEmployeesToExistingBatchesFromExcelDuplicateEmployees() {
	    Long batchId = 1L;
	    List<EmployeeDTO> employees = new ArrayList<>();
	    employees.add(new EmployeeDTO(1L));
	    employees.add(new EmployeeDTO(2L));

	    Batch batch = new Batch();
	    batch.setBatchId(batchId);
	    batch.setEmployeeId(List.of(1L, 2L));

	    when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

	    assertThrows(DuplicateEmployeeException.class,
	            () -> batchService.addEmployeesToExistingBatchesFromExcel(batchId, employees));

	    verify(batchRepository, times(1)).findById(batchId);
	    verify(batchRepository, never()).save(any());
	}


	@Test
	public void testAddEmployeesToExistingBatchFromExcel() throws BatchNotFoundException, DuplicateEmployeeException {
	    String batchName = "TestBatch";

	    List<EmployeeDTO> employees = new ArrayList<>();
	    employees.add(new EmployeeDTO(1L));
	    employees.add(new EmployeeDTO(2L));

	    Batch existingBatch = new Batch();
	    existingBatch.setBatchName(batchName);
	    existingBatch.setEmployeeId(List.of(1L, 2L, 3L)); // Existing employees in the batch

	    Mockito.when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(existingBatch));

	    // Simulate case where all employees are already in the batch
	    DuplicateEmployeeException exception = assertThrows(DuplicateEmployeeException.class,
	            () -> batchService.addEmployeesToExistingBatchFromExcel(batchName, employees));

	    assertEquals("All employees provided are already present in this batch", exception.getMessage());

	    // Verify that the batch was not saved
	    Mockito.verify(batchRepository, Mockito.never()).save(existingBatch);
	}


	@Test
	public void testAddEmployeesToExistingBatchFromExcelBatchNotFound() {
		List<EmployeeDTO> employees = new ArrayList<>();
		employees.add(new EmployeeDTO((long) 1));
		employees.add(new EmployeeDTO((long) 2));

		when(batchRepository.findByBatchName("NonExistentBatch")).thenReturn(Optional.empty());

		assertThrows(BatchNotFoundException.class,
				() -> batchService.addEmployeesToExistingBatchFromExcel("NonExistentBatch", employees));

		verify(batchRepository, times(1)).findByBatchName("NonExistentBatch");
		verify(batchRepository, never()).save(any());
	}

	
	@Test
	public void testGetAllBatchNames() {
	        // Given
	        Batch batch1 = new Batch();
	        batch1.setBatchName("Batch1");
	
	        Batch batch2 = new Batch();
	        batch2.setBatchName("Batch2");
	
	        List<Batch> batches = Arrays.asList(batch1, batch2);
	        Mockito.when(batchRepository.findAll()).thenReturn(batches);
	
	        // When
	        List<String> result = batchService.getAllBatchNames();
	
	        // Then
	        assertEquals(Arrays.asList("Batch1", "Batch2"), result);
	 }
	
	
     @Test
	 public void testGetAllBatchNamesEmpty() {
		List<Batch> batches = new ArrayList<>();

		when(batchRepository.findAll()).thenReturn(batches);

		Exception exception = assertThrows(BatchEmptyException.class, () -> batchService.getAllBatchNames());

		assertEquals("Batches are not created yet", exception.getMessage());

		verify(batchRepository, times(1)).findAll();
	 }


	@Test
	public void testGetBatchById() {
		Long batchId = 1L;
		Batch batch = new Batch();
		batch.setBatchId(batchId);

		when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

		ResponseEntity<Object> responseEntity = batchService.getBatchById(batchId);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(batch, responseEntity.getBody());

		verify(batchRepository, times(1)).findById(batchId);
	}
 
	
	@Test
	public void testGetBatchByIdNotFound() {
		Long batchId = 999L;

		when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(BatchNotFoundException.class, () -> batchService.getBatchById(batchId));

		assertEquals("Batch not found with id " + batchId, exception.getMessage());

		verify(batchRepository, times(1)).findById(batchId);
	}

	
	@Test
	public void testGetBatchByName() {
		String batchName = "TestBatch";
		Batch batch = new Batch();
		batch.setBatchName(batchName);

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));

		ResponseEntity<Object> responseEntity = batchService.getBatchByName(batchName);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(batch, responseEntity.getBody());

		verify(batchRepository, times(1)).findByBatchName(batchName);
	}

	
	@Test
	public void testGetBatchByNameNotFound() {
		String batchName = "NonExistentBatch";

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.empty());

		Exception exception = assertThrows(BatchNotFoundException.class, () -> batchService.getBatchByName(batchName));

		assertEquals("Batch not found with name " + batchName, exception.getMessage());

		verify(batchRepository, times(1)).findByBatchName(batchName);
	}

	
	@Test
	public void testGetAllBatches() {
		List<Batch> batches = new ArrayList<>();
		batches.add(new Batch());
		batches.add(new Batch());
		batches.add(new Batch());

		when(batchRepository.findAll()).thenReturn(batches);

		List<Batch> result = batchService.getAllBatches();

		assertEquals(batches.size(), result.size());
		assertEquals(batches.get(0), result.get(0));
		assertEquals(batches.get(1), result.get(1));
		assertEquals(batches.get(2), result.get(2));

		verify(batchRepository, times(1)).findAll();
	}

	
	@Test
	public void testGetAllBatchesEmpty() {
		List<Batch> batches = new ArrayList<>();

		when(batchRepository.findAll()).thenReturn(batches);

		Exception exception = assertThrows(BatchEmptyException.class, () -> batchService.getAllBatches());

		assertEquals("Batches are not created yet", exception.getMessage());

		verify(batchRepository, times(1)).findAll();
	}

	
	@Test
	public void testGetEmployeesInBatch() {
		Long batchId = 1L;

		Batch batch = new Batch();
		batch.setBatchId(batchId);
		List<Long> employeeIds = new ArrayList<>();
		employeeIds.add((long) 1);
		employeeIds.add((long) 2);
		batch.setEmployeeId(employeeIds);

		when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

		List<Long> result = batchService.getEmployeesInBatch(batchId);

		assertEquals(employeeIds, result);

		verify(batchRepository, times(1)).findById(batchId);
	}

	
	@Test
	public void testGetEmployeesInBatchBatchEmpty() {
		Long batchId = 1L;

		Batch batch = new Batch();
		batch.setBatchId(batchId);
		batch.setEmployeeId(new ArrayList<>());

		when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

		Exception exception = assertThrows(BatchEmptyException.class, () -> batchService.getEmployeesInBatch(batchId));

		assertEquals("No employees found in batch with id " + batchId, exception.getMessage());

		verify(batchRepository, times(1)).findById(batchId);
	}

	
	@Test
	public void testGetEmployeesInBatchBatchNotFound() {
		Long batchId = 1L;

		when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(BatchNotFoundException.class,
				() -> batchService.getEmployeesInBatch(batchId));

		assertEquals("Batch with id " + batchId + " not found.", exception.getMessage());

		verify(batchRepository, times(1)).findById(batchId);
	}

	
	@Test
	public void testGetEmployeesInBatchByName() {
		String batchName = "TestBatch";

		Batch batch = new Batch();
		batch.setBatchName(batchName);
		List<Long> employeeIds = new ArrayList<>();
		employeeIds.add((long) 1);
		employeeIds.add((long) 2);
		batch.setEmployeeId(employeeIds);

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));

		List<Long> result = batchService.getEmployeesInBatchByName(batchName);

		assertEquals(employeeIds, result);

		verify(batchRepository, times(1)).findByBatchName(batchName);
	}

	
	@Test
	public void testGetEmployeesInBatchByNameBatchEmpty() {
		String batchName = "EmptyBatch";

		Batch batch = new Batch();
		batch.setBatchName(batchName);
		batch.setEmployeeId(new ArrayList<>());

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));

		Exception exception = assertThrows(BatchEmptyException.class,
				() -> batchService.getEmployeesInBatchByName(batchName));

		assertEquals("No employees found in batch with name " + batchName, exception.getMessage());

		verify(batchRepository, times(1)).findByBatchName(batchName);
	}

	
	@Test
	public void testGetEmployeesInBatchByNameBatchNotFound() {
		String batchName = "NonExistentBatch";

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.empty());

		Exception exception = assertThrows(BatchNotFoundException.class,
				() -> batchService.getEmployeesInBatchByName(batchName));

		assertEquals("Batch with name " + batchName + " not found.", exception.getMessage());

		verify(batchRepository, times(1)).findByBatchName(batchName);
	}

	
	@Test
    void testGetAllBatchNamesWithIdsWhenBatchesNotEmpty() {
        // Mocking data
        List<Batch> batches = new ArrayList<>();
        Batch batch1 = new Batch(1L, "Batch A", "Description A", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 30), List.of(101L, 102L), 50L);
        Batch batch2 = new Batch(2L, "Batch B", "Description B", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31), List.of(103L, 104L), 60L);
        batches.add(batch1);
        batches.add(batch2);

        // Mocking repository method
        when(batchRepository.findAll()).thenReturn(batches);

        // Calling the service method
        List<Map<String, Object>> result = batchService.getAllBatchNamesWithIds();

        // Verifying result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        // Verifying content
        Map<String, Object> batch1Map = result.get(0);
        assertEquals(1L, batch1Map.get("batchId"));
        assertEquals("Batch A", batch1Map.get("batchName"));

        Map<String, Object> batch2Map = result.get(1);
        assertEquals(2L, batch2Map.get("batchId"));
        assertEquals("Batch B", batch2Map.get("batchName"));

        // Verifying repository method invocation
        verify(batchRepository, times(1)).findAll();
    }

	
    @Test
    void testGetAllBatchNamesWithIdsWhenBatchesEmpty() {
        // Mocking repository method to return an empty list
        when(batchRepository.findAll()).thenReturn(new ArrayList<>());

        // Calling the service method
        assertThrows(BatchEmptyException.class, () -> {
            batchService.getAllBatchNamesWithIds();
        });

        // Verifying repository method invocation
        verify(batchRepository, times(1)).findAll();
    }
	
	
	@Test
	public void testDeleteBatchById() {
		Long batchId = 1L;

		Batch batch = new Batch();
		batch.setBatchId(batchId);

		when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
		doNothing().when(batchRepository).delete(batch);

		assertDoesNotThrow(() -> batchService.deleteBatchById(batchId));

		verify(batchRepository, times(1)).findById(batchId);
		verify(batchRepository, times(1)).delete(batch);
	}

	
	@Test
	public void testDeleteBatchByIdBatchNotFound() {
		Long batchId = 1L;

		when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

		assertThrows(BatchNotFoundException.class, () -> batchService.deleteBatchById(batchId));

		verify(batchRepository, times(1)).findById(batchId);
		verify(batchRepository, never()).delete(any());
	}

	
	@Test
	public void testDeleteBatchByName() {
		String batchName = "Test Batch";

		Batch batch = new Batch();
		batch.setBatchName(batchName);

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.of(batch));
		doNothing().when(batchRepository).delete(batch);

		assertDoesNotThrow(() -> batchService.deleteBatchByName(batchName));

		verify(batchRepository, times(1)).findByBatchName(batchName);
		verify(batchRepository, times(1)).delete(batch);
	}

	
	@Test
	public void testDeleteBatchByNameBatchNotFound() {
		String batchName = "Test Batch";

		when(batchRepository.findByBatchName(batchName)).thenReturn(Optional.empty());

		assertThrows(BatchNotFoundException.class, () -> batchService.deleteBatchByName(batchName));

		verify(batchRepository, times(1)).findByBatchName(batchName);
		verify(batchRepository, never()).delete(any());
	}

	
	@Test
	public void testDeleteEmployeeFromBatch() {
		Long batchId = 1L;
		int employeeId = 101;

		Batch batch = new Batch();
		batch.setBatchId(batchId);
		List<Long> employeeIds = new ArrayList<>();
		employeeIds.add((long) employeeId);
		batch.setEmployeeId(employeeIds);

		when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
		when(batchRepository.save(batch)).thenReturn(batch);

		assertDoesNotThrow(() -> batchService.deleteEmployeeFromBatch(batchId, employeeId));

		verify(batchRepository, times(1)).findById(batchId);
		verify(batchRepository, times(1)).save(batch);
	}

	
	@Test
	public void testDeleteEmployeeFromBatchEmployeeFound() {
	    // Given
	    Long batchId = 1L;
	    Long employeeId = 1001L;
	    Batch batch = new Batch();
	    batch.setBatchId(batchId);
	    batch.setEmployeeId(Arrays.asList(1001L, 1002L, 1003L));
	    Mockito.when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

	    // When
	    batchService.deleteEmployeeFromBatch(batchId, employeeId);

	    // Then
	    assertFalse(batch.getEmployeeId().contains(employeeId));
	    Mockito.verify(batchRepository, Mockito.times(1)).save(batch);
	}


	@Test
	public void testDeleteEmployeeFromBatchEmployeeNotFound() {
	    // Given
	    Long batchId = 1L;
	    Long employeeId = 1004L; // Employee ID not in the batch
	    Batch batch = new Batch();
	    batch.setBatchId(batchId);
	    batch.setEmployeeId(Arrays.asList(1001L, 1002L, 1003L));
	    Mockito.when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

	    // When/Then
	    assertThrows(EmployeeNotFoundException.class, () -> batchService.deleteEmployeeFromBatch(batchId, employeeId));
	}

	
	@Test
	public void testDeleteEmployeeFromBatchBatchNotFound() {
	    // Given
	    Long batchId = 1L; // Batch ID not found
	    int employeeId = 1001;
	    Mockito.when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

	    // When/Then
	    assertThrows(BatchNotFoundException.class, () -> batchService.deleteEmployeeFromBatch(batchId, employeeId));
	}


	@Test
    void testUpdateEndDateWhenBatchExists() {
        // Mock data
        Long batchId = 1L;
        LocalDate endDate = LocalDate.of(2024, 4, 30);
        Batch batch = new Batch(batchId, "Batch A", "Description", LocalDate.of(2024, 4, 1),
                endDate, null, 50L);

        // Mock repository method
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));

        // Calling the service method
        assertDoesNotThrow(() -> {
            batchService.updateEndDate(batchId, endDate);
        });

        // Verifying repository method invocation
        verify(batchRepository, times(1)).findById(batchId);
        verify(batchRepository, times(1)).save(batch);
    }

	
    @Test
    void testUpdateEndDateWhenBatchNotExists() {
        // Mock data
        Long batchId = 1L;
        LocalDate endDate = LocalDate.of(2024, 4, 30);

        // Mock repository method
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Calling the service method and verifying exception
        BatchNotFoundException exception = assertThrows(BatchNotFoundException.class, () -> {
            batchService.updateEndDate(batchId, endDate);
        });

        assertEquals("Batch with id " + batchId + " not found", exception.getMessage());

        // Verifying repository method invocation
        verify(batchRepository, times(1)).findById(batchId);
        verify(batchRepository, never()).save(any());
    }

	
	@Test
	public void testUpdateBatchName() {
		Long id = 1L;
		String batchName = "New Batch Name";

		Batch batch = new Batch();
		batch.setBatchId(id);
		batch.setBatchName("Old Batch Name");

		when(batchRepository.findById(id)).thenReturn(Optional.of(batch));
		when(batchRepository.existsByBatchName(batchName)).thenReturn(false);

		batchService.updateBatchName(id, batchName);

		assertEquals(batchName, batch.getBatchName());
		verify(batchRepository, times(1)).save(batch);
	}
	
	@Test
    public void testUpdateBatch() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchName("TestBatch");
        batch.setBatchDescription("Description");
        batch.setStartDate(LocalDate.now());
        batch.setEndDate(LocalDate.now());
        batch.setBatchSize(10L);

        // Mock behavior of findById
        Batch existingBatch = new Batch();
        existingBatch.setBatchId(batchId);
        existingBatch.setBatchName("ExistingBatch");
        existingBatch.setBatchDescription("Existing Description");
        existingBatch.setStartDate(LocalDate.now());
        existingBatch.setEndDate(LocalDate.now());
        existingBatch.setBatchSize(5L);
        when(batchRepository.findById(batchId)).thenReturn(Optional.of(existingBatch));

        // Call the method
        batchService.updateBatch(batchId, batch);

        // Verify that batchRepository.save(existingBatch) was called once
        verify(batchRepository, times(1)).save(existingBatch);

        // Verify that the existingBatch has been updated with the new values
        assertEquals("TestBatch", existingBatch.getBatchName());
        assertEquals("Description", existingBatch.getBatchDescription());
        // Add more assertions as needed
    }

    @Test
    public void testUpdateBatchNotFound() {
        // Mock data
        Long batchId = 1L;
        Batch batch = new Batch();
        batch.setBatchName("TestBatch");
        batch.setBatchDescription("Description");
        batch.setStartDate(LocalDate.now());
        batch.setEndDate(LocalDate.now());
        batch.setBatchSize(10L);

        // Mock behavior of findById
        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        // Call the method, should throw BatchNotFoundException
        assertThrows(BatchNotFoundException.class, () -> batchService.updateBatch(batchId, batch));
    }
}
