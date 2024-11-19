package com.ssafy.edu.controller;

import com.ssafy.edu.model.Board;
import com.ssafy.edu.model.BoardFile;
import com.ssafy.edu.service.BoardService;
import com.ssafy.edu.util.FileUploadUtil;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.github.pagehelper.PageInfo;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@AllArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createBoard(
            @RequestPart("board") Board board,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    	//log.debug("================>{}",board);
        return ResponseEntity.ok(boardService.createBoard(board, files));
    }
//http://localhost:8080/api/boards/지금 작성한 보드아이디 id
  //http://localhost:8080/api/boards/detail/22
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardWithFiles(id));
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getBoardDetail(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardFileDetail(id));
    }
    
    @GetMapping
    public ResponseEntity<PageInfo<Board>> getBoards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(boardService.getBoards(page, size, searchType, keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long id,
            @RequestPart("board") Board board,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds) {
        board.setId(id);
        return ResponseEntity.ok(boardService.updateBoard(board, files, deleteFileIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok().build();
    }
}