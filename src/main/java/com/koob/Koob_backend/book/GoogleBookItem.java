package com.koob.Koob_backend.book;

import lombok.Data;

@Data
public class GoogleBookItem {
    private String kind;
    private String id;
    private String etag;
    private String selfLink;
    private VolumeInfo volumeInfo;
}
