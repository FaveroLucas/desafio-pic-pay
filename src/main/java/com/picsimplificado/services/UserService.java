package com.picsimplificado.services;

import com.picsimplificado.domain.user.User;
import com.picsimplificado.dtos.UserDTO;
import com.picsimplificado.enums.UserType;
import com.picsimplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {

        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usuario do tipo lojista não pode realizar transações");
        }

        if(sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception {
        return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    public User createUser(UserDTO userDTO) {

        User user = new User(userDTO);
        this.saveUser(user);
        return user;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }
}
