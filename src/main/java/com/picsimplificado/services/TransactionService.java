package com.picsimplificado.services;

import com.picsimplificado.domain.transaction.Transaction;
import com.picsimplificado.domain.user.User;
import com.picsimplificado.dtos.TransactionDTO;
import com.picsimplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transactionDto) throws Exception {
        User sender = this.userService.findUserById(transactionDto.senderId());
        User receiver = this.userService.findUserById(transactionDto.receiverId());

        userService.validateTransaction(sender, transactionDto.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transactionDto.value());
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        Transaction transaction = new Transaction();

        transaction.setAmount(transactionDto.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDto.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDto.value()));

        this.transactionRepository.save(transaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Sua transação foi concluída");
        this.notificationService.sendNotification(receiver, "Sua transação foi concluída");

        return transaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizarionResponse = restTemplate.getForEntity("https://run.mocky.io/v3/9d9073fb-9833-4b01-82d3-0b43bfa6e0b6", Map.class);

        if(authorizarionResponse.getStatusCode() == HttpStatus.OK){
            String message = authorizarionResponse.getBody().get("message").toString();
            return "Autorizado".equalsIgnoreCase(message);
        } else return false;
    }
}
