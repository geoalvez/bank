package com.geoalvez.bank.repository;

import com.geoalvez.bank.model.Conta;
import com.geoalvez.bank.model.Transacao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface TransacaoRepository  extends CrudRepository<Transacao, Long> {

    List<Transacao> findByIdConta(Long idConta);
    List<Transacao> findByIdContaAndDataTransacaoBetween(Long idConta, Date dataInicio, Date dataFim);

}
