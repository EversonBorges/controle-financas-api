package br.com.controlefinancas.api.domain.card;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long>{

	Page<Card> findAllByActiveTrue(Pageable pageable);
}
