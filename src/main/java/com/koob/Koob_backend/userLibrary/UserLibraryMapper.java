package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.book.BookMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface UserLibraryMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "book", target = "book")
    UserLibraryDTO toDto(UserLibrary userLibrary);
}
