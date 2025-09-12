package com.koob.Koob_backend.book;

import lombok.Data;

import java.util.List;

@Data
public class VolumeInfo {
    private String title;
    private String subtitle;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private Integer pageCount;
    private ImageLinks imageLinks;
    private String language;
    private String previewLink;
    private String infoLink;
}
