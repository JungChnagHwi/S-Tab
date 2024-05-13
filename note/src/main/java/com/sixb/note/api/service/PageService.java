package com.sixb.note.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixb.note.dto.page.*;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.dto.pageData.PageDataDto;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.exception.PageNotFoundException;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final NoteRepository noteRepository;

    public PageCreateResponseDto createPage(PageCreateRequestDto request) throws PageNotFoundException {
        String beforePageId = request.getBeforePageId();

        // id로 이전 페이지 정보를 찾아
        Optional<Page> beforePageOptional = Optional.ofNullable(pageRepository.findPageById(beforePageId));

        // 이전 페이지 정보가 있다면
        if (beforePageOptional.isPresent()) {
            Page beforePage = beforePageOptional.get();
            Page newPage = new Page();
            LocalDateTime now = LocalDateTime.now();

            // 이전페이지 정보로 새로운 page만들기
            String pageId = IdCreator.create("p");
            newPage.setPageId(pageId);
            newPage.setNoteId(beforePage.getNoteId());
            newPage.setCreatedAt(now);
            newPage.setUpdatedAt(now);
            newPage.setColor(beforePage.getColor());
            newPage.setDirection(beforePage.getDirection());
            newPage.setPageData("{\"paths\": [], \"figures\": [],\"textBoxes\": [], \"images\": []}");
            // 만약 이전 페이지가 pdf페이지가 아니라면
            if (beforePage.getPdfUrl()==null) {
                newPage.setTemplate(beforePage.getTemplate());
            } else { // pdf인 경우 기본 템플릿
                // 현재는 내 마음대로 넣어둠
                newPage.setTemplate("basic");
            }

            // 앞페이지에 이어진 페이지 찾기
            Page connectPage = pageRepository.getNextPageByPageId(beforePageId);
            // 페이지가 있다면
            if (connectPage!=null) {
                // 그 페이지와 새로운 페이지 연결
                newPage.setNextPage(connectPage);
                // 앞페이지와 연결 삭제
                pageRepository.deleteNextPageRelation(beforePageId);
            }

            // 페이지 링크하기
            beforePage.setNextPage(newPage);

            // responsedto에 넣기
            PageCreateResponseDto responseDto = PageCreateResponseDto.builder()
                    .pageId(pageId)
                    .color(beforePage.getColor())
                    .template(beforePage.getTemplate())
                    .direction(beforePage.getDirection())
                    .updatedAt(now)
                    .build();

            // db에 저장하고 반환
            pageRepository.save(newPage);
            pageRepository.save(beforePage);

            return responseDto;
        } else { // page 못찾은 경우
            // error
            throw new PageNotFoundException("이전 페이지 정보가 없습니다.");
        }

    }

    public void deletePage(String pageId) throws PageNotFoundException {
        Page page = pageRepository.findPageById(pageId);
        if (page != null) {
            Boolean deleteStatus = page.getIsDeleted();
            if (deleteStatus == false) {
                page.setIsDeleted(true);
                pageRepository.save(page);
            } else {
                throw new PageNotFoundException("이미 삭제된 페이지입니다.");
            }
        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }
    }

    public void saveData(SaveDataRequestDto request) throws PageNotFoundException {
//        System.out.println("request:" + request);
        String pageId = request.getPageId();
        Page page = pageRepository.findPageById(pageId);

        LocalDateTime now = LocalDateTime.now();

        if (page != null) {
            Note note = noteRepository.findNoteById(page.getNoteId());
            if (note == null) {
                throw new PageNotFoundException("노트를 찾을 수 없습니다.");
            }
            if (page.getIsDeleted() == false) {
                page.setUpdatedAt(now);
                page.setPageData(request.getPageData());
                note.setUpdatedAt(now);
                pageRepository.save(page);
                noteRepository.save(note);
//                System.out.println("noteId: "+note.getNoteId());
            } else {
                throw new PageNotFoundException("이미 삭제된 페이지입니다.");
            }

        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }
    }

    public PageUpdateDto updatePage(PageUpdateDto request) throws PageNotFoundException {
        String pageId = request.getPageId();
        Page page = pageRepository.findPageById(pageId);
        if (page != null) {
            Boolean deleteStatus = page.getIsDeleted();
            if (deleteStatus == false) {
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

    // 페이지 조회
    public PageListResponseDto getPageList(long userId, String noteId) throws NoteNotFoundException, PageNotFoundException, JsonProcessingException {
        Note note = noteRepository.findNoteById(noteId);
        if (note != null) {
            List<PageInfoDto> pageInfoList = new ArrayList<>();

            // noteId에 연결되어있는 페이지 모두 불러오기
            List<Page> pageList = pageRepository.findAllPagesByNoteId(noteId);
            System.out.println("noteId: "+noteId);

            System.out.println(pageList.size());

            for (Page page : pageList) {
                // pageData 역직렬화
                String pageDataString = page.getPageData();
                ObjectMapper mapper = new ObjectMapper();
                PageDataDto pageDataDto = mapper.readValue(pageDataString, PageDataDto.class);
                String pageId = page.getPageId();

                // pageInfoDto에 넣기
                PageInfoDto pageInfoDto = PageInfoDto.builder()
                        .pageId(pageId)
                        .color(page.getColor())
                        .template(page.getTemplate())
                        .direction(page.getDirection())
                        .pdfPage(page.getPdfPage())
                        .pdfUrl(page.getPdfUrl())
                        .updatedAt(page.getUpdatedAt())
                        .isBookmarked(pageRepository.isLikedByPageId(userId, pageId))
                        .paths(pageDataDto.getPaths())
                        .figures(pageDataDto.getFigures())
                        .images(pageDataDto.getImages())
                        .textBoxes(pageDataDto.getTextBoxes())
                        .build();
                // pageInfoList에 넣기
                pageInfoList.add(pageInfoDto);
            }

            PageListResponseDto pageListResponseDto = PageListResponseDto.builder()
                    .data(pageInfoList)
                    .title(note.getTitle())
                    .build();

            return pageListResponseDto;
        } else {
            throw new NoteNotFoundException("노트를 찾을 수 없습니다.");
        }

    }

    // 페이지 링크 - 보류
//    public void linkPage(PageLinkRequestDto request) throws PageNotFoundException {
//        Page linkPage = pageRepository.findPageById(request.getLinkPageId());
//        Page targetPage = pageRepository.findPageById(request.getTargetPageId());
//    }

    public PageInfoDto copyPage(PageCopyRequestDto request) throws PageNotFoundException, JsonProcessingException {
        String beforePageId = request.getBeforePageId();
        Page beforePage = pageRepository.findPageById(beforePageId);
        Page targetPage = pageRepository.findPageById(request.getTargetPageId());

        if (beforePage != null && targetPage != null) {
            // before 페이지에 이어서 페이지 만들기
            Page newPage = new Page();
            LocalDateTime now = LocalDateTime.now();

            // 이전페이지 정보로 새로운 page만들기
            String pageId = IdCreator.create("p");
            newPage.setPageId(pageId);
            newPage.setNoteId(beforePage.getNoteId());
            newPage.setCreatedAt(now);
            newPage.setUpdatedAt(now);
            newPage.setColor(targetPage.getColor());
            newPage.setDirection(targetPage.getDirection());
            newPage.setPageData(targetPage.getPageData());
            newPage.setTemplate(targetPage.getTemplate());

            // 이전페이지에 이어진 페이지 찾기
            Page connectPage = pageRepository.getNextPageByPageId(beforePageId);
            // 페이지가 있다면
            if (connectPage!=null) {
                // 그 페이지와 새로운 페이지 연결
                newPage.setNextPage(connectPage);
                // 앞페이지와 연결 삭제
                pageRepository.deleteNextPageRelation(beforePageId);
            }

            // 페이지 링크하기
            beforePage.setNextPage(newPage);

            // responsedto에 넣기
            String pageDataString = newPage.getPageData();
            ObjectMapper mapper = new ObjectMapper();
            PageDataDto pageDataDto = mapper.readValue(pageDataString, PageDataDto.class);
            PageInfoDto response = PageInfoDto.builder()
                    .pageId(pageId)
                    .color(newPage.getColor())
                    .template(newPage.getTemplate())
                    .direction(newPage.getDirection())
                    .pdfPage(newPage.getPdfPage())
                    .pdfUrl(newPage.getPdfUrl())
                    .updatedAt(newPage.getUpdatedAt())
                    .isBookmarked(false)
                    .paths(pageDataDto.getPaths())
                    .figures(pageDataDto.getFigures())
                    .images(pageDataDto.getImages())
                    .textBoxes(pageDataDto.getTextBoxes())
                    .build();

            // db에 저장하고 반환
            pageRepository.save(newPage);
            pageRepository.save(beforePage);

            return response;

        } else {
            throw new PageNotFoundException("페이지를 찾을 수 없습니다.");
        }
    }
    
}
