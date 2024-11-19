package com.ssafy.edu.mapper;

import com.ssafy.edu.model.BoardFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardFileMapper {
    void insert(BoardFile boardFile);
    void insertBatch(List<BoardFile> files);
    BoardFile findById(Long id);
    List<BoardFile> findByBoardId(Long boardId);
    List<BoardFile> findByIds(List<Long> ids);
    void deleteById(Long id);
    void deleteByIds(List<Long> ids);
    void deleteByBoardId(Long boardId);
    List<String> findStoredFilenamesByBoardId(Long boardId);
}