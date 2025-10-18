package com.koob.Koob_backend.libraryItem;

import com.koob.Koob_backend.book.BookMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface LibraryItemMapper {
    @Mapping(source = "book", target = "book")
    LibraryItemDTO toDto(LibraryItem libraryItem);
}
