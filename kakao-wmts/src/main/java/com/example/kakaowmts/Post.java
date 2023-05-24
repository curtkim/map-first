package com.example.kakaowmts;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Post {
  private long id;
  private String title;
  private String content;
}
