package com.practice.shareitzeinolla.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.shareitzeinolla.item.Comment;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @OneToMany(mappedBy = "request")
    @JsonIgnore
    private List<Item> items;
}
