package com.ssafy.edu.mapper;

import com.ssafy.edu.model.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BoardMapper {
    void insert(Board board);
    Board findById(Long id);
    List<Board> findAll(@Param("searchType") String searchType, @Param("keyword") String keyword);
    void update(Board board);
    void delete(Long id);
    void incrementViewCount(Long id);
}