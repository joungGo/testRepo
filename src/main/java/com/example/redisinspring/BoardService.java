package com.example.redisinspring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    private final CacheManager boardCacheManager;
    private BoardRepository boardRepository;


    public BoardService(BoardRepository boardRepository, CacheManager boardCacheManager) {
        this.boardRepository = boardRepository;
        this.boardCacheManager = boardCacheManager;
    }

    // boards:page:{page}:size:{size}
    /*
    @Cacheable 어노테이션을 붙이면 Cache Aside 전략으로 캐싱이 적용된다.
    즉, 해당 메서드로 요청이 들어오면 레디스를 확인한 후에 데이터가 있다면 레디스의 데이터를 조회해서 바로 응답한다.
    만약 데이터가 없다면 메서드 내부의 로직을 실행시킨 뒤에 return 값으로 응답한다. 그리고 그 return 값을 레디스에 저장한다.
    */
    @Cacheable(cacheNames = "getBoards", key = "'boards:page' + #page + ':size' + #size", cacheManager = "boardCacheManager")
    public List<Board> getBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Board> pageOfBoards = boardRepository.findAllByOrderByCreatedAtDesc(pageable);
        return pageOfBoards.getContent();
    }
}
