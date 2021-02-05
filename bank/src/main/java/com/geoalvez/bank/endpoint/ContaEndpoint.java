package com.geoalvez.bank.endpoint;

import com.geoalvez.bank.error.CustomErrorType;
import com.geoalvez.bank.model.Conta;
import com.geoalvez.bank.model.Transacao;
import com.geoalvez.bank.repository.ContaRepository;
import com.geoalvez.bank.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("contas")
@ComponentScan
public class ContaEndpoint {

    private ContaRepository contaDao;
    private TransacaoRepository transacaoDao;

    public TransacaoRepository getTransacaoDao() {
        return transacaoDao;
    }

    public void setTransacaoDao(TransacaoRepository transacaoDao) {
        this.transacaoDao = transacaoDao;
    }

    @Autowired
    public ContaEndpoint (ContaRepository contaDao, TransacaoRepository transacaoDao){
        this.contaDao = contaDao;
        this.transacaoDao = transacaoDao;
    }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listAll(){

        return new ResponseEntity<>(contaDao.findAll(), HttpStatus.OK);

    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getContaById(@PathVariable("id") Long id){

        Optional<Conta> conta = this.contaDao.findById(id);
        if (conta == null){
            return new ResponseEntity<>(new CustomErrorType("Conta nao encontrada"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(conta, HttpStatus.OK);
    }


    @GetMapping(path="/extrato/{id}")
    public ResponseEntity<?> getTransacoesByIdConta(@PathVariable("id") Long id){
        return new ResponseEntity<>(this.transacaoDao.findByIdConta(id), HttpStatus.OK);
    }


    @GetMapping(path="/extrato/{id}/{dataInicio}/{dataFim}")
    public ResponseEntity<?> getTransacoesByIdContaBetweenDates(@PathVariable("id") Long id, @PathVariable("dataInicio") String dataInicio,
                                                    @PathVariable("dataFim") String dataFim){

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
        Date dataInicioDate = null;
        Date dataFimDate = null;

        try {
            dataInicioDate = formatter.parse(dataInicio);
            dataFimDate = formatter.parse(dataFim);
        } catch (ParseException e) {
            return new ResponseEntity<>(new CustomErrorType("Data Inicio ou Data Fim Invalidas"), HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(this.transacaoDao.findByIdContaAndDataTransacaoBetween(id, dataInicioDate, dataFimDate), HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<?> save (@RequestBody Conta conta){
        return new ResponseEntity<>(contaDao.save(conta), HttpStatus.OK);
    }

    @PostMapping(path="/deposito")
    public ResponseEntity<?> depositar (@RequestBody Transacao transacao){
        Optional<Conta> contaTransacao = contaDao.findById(transacao.getIdConta());
        if (!contaTransacao.isPresent()){
            return new ResponseEntity<>(new CustomErrorType("Conta nao encontrada"), HttpStatus.NOT_FOUND);
        }

        if (transacao.getValor().compareTo(BigDecimal.ZERO) < 0){
            return new ResponseEntity<>(new CustomErrorType("Deposito nao pode ter valor negativo"), HttpStatus.BAD_REQUEST);
        }

        Conta conta = contaTransacao.get();
        conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
        transacaoDao.save(transacao);

        return new ResponseEntity<>(contaDao.save(conta), HttpStatus.OK);
    }


    @PostMapping(path="/bloqueio/{id}")
    public ResponseEntity<?> bloquearConta (@PathVariable("id") Long id){
        Optional<Conta> contaTransacao = contaDao.findById(transacao.getIdConta());
        if (!contaTransacao.isPresent()){
            return new ResponseEntity<>(new CustomErrorType("Conta nao encontrada"), HttpStatus.NOT_FOUND);
        }

        if (transacao.getValor().compareTo(BigDecimal.ZERO) < 0){
            return new ResponseEntity<>(new CustomErrorType("Deposito nao pode ter valor negativo"), HttpStatus.BAD_REQUEST);
        }

        Conta conta = contaTransacao.get();
        conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
        transacaoDao.save(transacao);

        return new ResponseEntity<>(contaDao.save(conta), HttpStatus.OK);
    }


    @PostMapping(path="/saque")
    public ResponseEntity<?> sacar (@RequestBody Transacao transacao){
        Optional<Conta> contaTransacao = contaDao.findById(transacao.getIdConta());
        if (!contaTransacao.isPresent()){
            return new ResponseEntity<>(new CustomErrorType("Conta nao encontrada"), HttpStatus.NOT_FOUND);
        }

        if (transacao.getValor().compareTo(BigDecimal.ZERO) < 0){
            return new ResponseEntity<>(new CustomErrorType("Saque nao pode ter valor negativo"), HttpStatus.BAD_REQUEST);
        }


        Conta conta = contaTransacao.get();
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValor()));
        transacao.setValor(transacao.getValor().multiply(new BigDecimal(-1)));
        transacaoDao.save(transacao);

        return new ResponseEntity<>(contaDao.save(conta), HttpStatus.OK);
    }




    @DeleteMapping (path = "/{id}")
    public ResponseEntity<?> delete (@PathVariable Long id) {
        contaDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Conta conta){
        contaDao.save(conta);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ContaRepository getContaDao() {
        return contaDao;
    }

    public void setContaDao(ContaRepository contaDao) {
        this.contaDao = contaDao;
    }
}
