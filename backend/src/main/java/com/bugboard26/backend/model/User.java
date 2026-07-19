package com.bugboard26.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


//Questa classe corrisponde ad una tabella nel databse
@Entity
@Table(name="users")

public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Databse genera automaticamente id
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    //Costruttore vuoto
    public User(){

    }

    public User(String email, String password, Role role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //getters e setters
    public Long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }
}
