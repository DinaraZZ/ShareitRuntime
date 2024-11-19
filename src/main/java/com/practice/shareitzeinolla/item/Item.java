package com.practice.shareitzeinolla.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.shareitzeinolla.request.Request;
import com.practice.shareitzeinolla.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "items")
public class Item {
    public Item() {
    }

    public Item(Long id) {
        this.id = id;
    }

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    private List<Comment> comments;
}
