package com.sixb.note.api.service;

import com.sixb.note.dto.page.PageCreateRequestDto;
import com.sixb.note.dto.page.PageCreateResponseDto;
import com.sixb.note.entity.Page;
import com.sixb.note.exception.PageNotFoundException;
import com.sixb.note.jwt.JwtTokenProvider;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public PageCreateResponseDto createPage(String token, PageCreateRequestDto request) throws InvalidTokenException, PageNotFoundException {
        long userId = jwtTokenProvider.getUserId(token);
        // token 으로 유효성 검사하는 부분 나중에 추가하기
        
        String beforeNoteId = request.getBeforePageId();

        // id로 이전 페이지 정보를 찾아
        Optional<Page> beforePageOptional = pageRepository.findById(beforeNoteId);

        // 이전 페이지 정보가 있다면
        if (beforePageOptional.isPresent()) {
            Page beforePage = beforePageOptional.get();
            Page newPage = new Page();
            LocalDateTime now = LocalDateTime.now();

            // 이전페이지 정보로 새로운 page만들기
            String pageId = IdCreator.create("p");
            newPage.setId(pageId);
            newPage.setCreatedAt(now);
            newPage.setColor(beforePage.getColor());
            newPage.setDirection(beforePage.getDirection());
            // 만약 이전 페이지가 pdf페이지가 아니라면
            if (beforePage.getPdfUrl()!=null) {
                newPage.setTemplate(beforePage.getTemplate());
            } else { // pdf인 경우 기본 템플릿
                // 현재는 내 마음대로 넣어둠
                newPage.setTemplate("basic");
            }
            // 앞 페이지 링크하기
            newPage.setPreviousPage(beforePage);

            // responsedto에 넣기
            PageCreateResponseDto responseDto = PageCreateResponseDto.builder()
                    .color(beforePage.getColor())
                    .template(beforePage.getTemplate())
                    .direction(beforePage.getDirection())
                    .build();

            // db에 저장하고 반환
            pageRepository.save(newPage);
            return responseDto;
        } else { // page 못찾은 경우
            // error
            throw new PageNotFoundException("이전 페이지 정보가 없습니다.");
        }

    }

    public void deletePage(String token, String pageId) throws InvalidTokenException, PageNotFoundException {
        long userId = jwtTokenProvider.getUserId(token);
        // 유효성 검사하는 부분 나중에 추가하기

        Optional<Page> optionalPage = pageRepository.findById(pageId);
        if (optionalPage.isPresent()) {
            Page page = optionalPage.get();
            int deleteStatus = page.getIsDelete();
            if (deleteStatus == 0) {
                page.setIsDelete(1);
            } else {
                throw new PageNotFoundException("이미 삭제된 페이지입니다.");
            }
        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }
    }
}
