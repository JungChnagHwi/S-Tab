package com.sixb.note.api.service;

import com.sixb.note.dto.page.*;
import com.sixb.note.entity.Page;
import com.sixb.note.entity.PageData;
import com.sixb.note.exception.PageNotFoundException;
import com.sixb.note.repository.PageDataRepository;
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

    @Autowired
    private PageDataRepository pageDataRepository;

    public PageCreateResponseDto createPage(PageCreateRequestDto request) throws PageNotFoundException {
        String beforeNoteId = request.getBeforePageId();

        // id로 이전 페이지 정보를 찾아
        Optional<Page> beforePageOptional = Optional.ofNullable(pageRepository.findPageById(beforeNoteId));

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
            if (beforePage.getNextPage() != null) {
                newPage.setNextPage(beforePage.getNextPage());
            }
            beforePage.setNextPage(newPage);

            // responsedto에 넣기
            PageCreateResponseDto responseDto = PageCreateResponseDto.builder()
                    .pageId(pageId)
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

    public void deletePage(String pageId) throws PageNotFoundException {
        Optional<Page> optionalPage = pageRepository.findById(pageId);
        if (optionalPage.isPresent()) {
            Page page = optionalPage.get();
            Boolean deleteStatus = page.getIsDeleted();
            if (deleteStatus == false) {
                page.setIsDeleted(true);
            } else {
                throw new PageNotFoundException("이미 삭제된 페이지입니다.");
            }
        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }
    }

    // 데이터 저장
    public void saveData(SaveDataRequestDto request) throws PageNotFoundException {
        String pageId = request.getPageId();
        Optional<Page> optionalPage = pageRepository.findById(pageId);
        if (optionalPage.isPresent()) {
            Page page = optionalPage.get();
            Boolean deleteStatus = page.getIsDeleted();
            if (deleteStatus == false) {
                // 필기데이터가 있는지 확인 후 있으면 삭제
                Optional<PageData> optionalPageData = pageDataRepository.findById(pageId);
                optionalPageData.ifPresent(pageData -> pageDataRepository.delete(pageData));
                // 필기 데이터 저장
                PageData pageData = PageData.builder()
                        .id(pageId)
                        .figures(request.getFigures())
                        .paths(request.getPaths())
                        .images(request.getImages())
                        .textBoxes(request.getTextBoxes())
                        .build();

                pageDataRepository.save(pageData);
            } else {
                throw new PageNotFoundException("이미 삭제된 페이지입니다.");
            }
        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }

    }

    public PageUpdateDto updatePage(PageUpdateDto request) throws PageNotFoundException {
        String pageId = request.getPageId();
        Optional<Page> optionalPage = pageRepository.findById(pageId);
        if (optionalPage.isPresent()) {
            Page page = optionalPage.get();
            Boolean deleteStatus = page.getIsDeleted();
            if (deleteStatus == false) {
                // 필기데이터가 있는지 확인 후
                Optional<PageData> optionalPageData = pageDataRepository.findById(pageId);
                // 양식 정보 수정
                page.setTemplate(request.getTemplate());
                page.setColor(request.getColor());
                page.setDirection(request.getDirection());

                pageRepository.save(page);

                return request;
            } else {
                throw new PageNotFoundException("이미 삭제된 페이지입니다.");
            }
        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }
    }
}
