package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.user.User;
import lombok.Data;

@Data
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
}
