package br.com.controlefinancas.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.controlefinancas.api.domain.transaction.RequestTransactionDto;
import br.com.controlefinancas.api.domain.transaction.RequestTransactionUpdateDto;
import br.com.controlefinancas.api.domain.transaction.ResponseTransactionDto;
import br.com.controlefinancas.api.domain.transaction.TransactionRepository;
import br.com.controlefinancas.api.domain.transaction.TransactionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("transactions")
public class TransactionController {

	@Autowired 
	private TransactionRepository repository;
	
	@Autowired
	private TransactionService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity<?> registerTransaction(@Valid @RequestBody RequestTransactionDto transactionDto, UriComponentsBuilder uriBuilder){
		service.createTransaction(transactionDto);
		var uri = uriBuilder.path("/transactions/all/{id}").buildAndExpand(transactionDto.idCard()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@GetMapping("card_id/{id}")
	public ResponseEntity<Page<ResponseTransactionDto>> getAllTransactionsByCardId(Pageable pageable,  @PathVariable Long id, String referenceDate){
		Map<String, String> mapDate = service.getMonthAndYearDateString(referenceDate);
		var page = repository.findAllTransactionsByCardIdAndMonthAndYearReferences(pageable, id, mapDate.get("MONTH"), mapDate.get("YEAR"))
				.map(ResponseTransactionDto::new);
		
		return ResponseEntity.ok(page);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getTransactionById(@PathVariable Long id){
		var transaction = repository.getReferenceById(id);
		
		return ResponseEntity.ok(new ResponseTransactionDto(transaction));
	}
	
	@PutMapping
	@Transactional
	public ResponseEntity<?> updateTransaction(@Valid @RequestBody RequestTransactionUpdateDto dto){
		var transaction = service.update(dto);
		
		return ResponseEntity.ok(new ResponseTransactionDto(transaction));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> removeTransaction(@PathVariable Long id){
		repository.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}
}
