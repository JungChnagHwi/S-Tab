package com.sixb.note.api.service;

import com.sixb.note.dto.page.*;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.entity.PageData;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.exception.PageNotFoundException;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageDataRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageDataRepository pageDataRepository;

    @Autowired
    private NoteRepository noteRepository;

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
            newPage.setUpdatedAt(now);
            newPage.setColor(beforePage.getColor());
            newPage.setDirection(beforePage.getDirection());
            // 만약 이전 페이지가 pdf페이지가 아니라면
            if (beforePage.getPdfUrl()==null) {
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

    // 데이터 저장
    public void saveData(SaveDataRequestDto request) throws PageNotFoundException {
        String pageId = request.getPageId();
        Page page = pageRepository.findPageById(pageId);
        if (page!=null) {
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
        Page page = pageRepository.findPageById(pageId);
        if (page != null) {
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

    // 페이지 조회
    public PageListResponseDto getPageList(long userId, String noteId) throws NoteNotFoundException {
        Note note = noteRepository.findNoteById(noteId);
        if (note != null) {
            List<PageInfoDto> pageInfoList = new ArrayList<>();

            // 일단 구현 후 하위부분 do-while이나 while로 바꿀 예정

            // noteId에 연결되어있는 페이지 불러오기
            Page firstPage = pageRepository.findFirstPageByNoteId(noteId);
            // 그 페이지에 해당하는 data 불러오기
            PageData firstPageData = pageDataRepository.findById(firstPage.getId()).orElse(null);
            String fistPageId = firstPage.getId();
            if (firstPageData != null) {
                // dto 빌드
                PageInfoDto pageInfoDto = PageInfoDto.builder()
                        .pageId(fistPageId)
                        .color(firstPage.getColor())
                        .template(firstPage.getTemplate())
                        .direction(firstPage.getDirection())
                        .updatedAt(firstPage.getUpdatedAt())
                        .isBookmarked(pageRepository.isLikedByPageId(userId, fistPageId))
                        .pdfUrl(firstPage.getPdfUrl())
                        .pdfPage(firstPage.getPdfPage())
                        .paths(firstPageData.getPaths())
                        .figures(firstPageData.getFigures())
                        .textBoxes(firstPageData.getTextBoxes())
                        .images(firstPageData.getImages())
                        .build();
                // List에 넣기
                pageInfoList.add(pageInfoDto);
            } else {
                // dto 빌드
                PageInfoDto pageInfoDto = PageInfoDto.builder()
                        .pageId(fistPageId)
                        .color(firstPage.getColor())
                        .template(firstPage.getTemplate())
                        .direction(firstPage.getDirection())
                        .updatedAt(firstPage.getUpdatedAt())
                        .isBookmarked(pageRepository.isLikedByPageId(userId, fistPageId))
                        .pdfUrl(firstPage.getPdfUrl())
                        .pdfPage(firstPage.getPdfPage())
                        .build();
                // List에 넣기
                pageInfoList.add(pageInfoDto);
            }

//            Page nextPage = firstPage.getNextPage();
            Page nextPage = pageRepository.getNextPageByPageId(fistPageId);


            while (nextPage != null) { // 다음 페이지가 없을때까지
                // 그 페이지에 해당하는 data 불러오기
                String nextPageId = nextPage.getId();
                PageData nextData = pageDataRepository.findById(nextPageId).orElse(null);
                // dto 빌드
                if (nextData != null) {
                    PageInfoDto nextPageInfoDto = PageInfoDto.builder()
                            .pageId(nextPageId)
                            .color(nextPage.getColor())
                            .template(nextPage.getTemplate())
                            .direction(nextPage.getDirection())
                            .updatedAt(nextPage.getUpdatedAt())
                            .isBookmarked(pageRepository.isLikedByPageId(userId, nextPageId))
                            .pdfUrl(nextPage.getPdfUrl())
                            .pdfPage(nextPage.getPdfPage())
                            .paths(nextData.getPaths())
                            .figures(nextData.getFigures())
                            .textBoxes(nextData.getTextBoxes())
                            .images(nextData.getImages())
                            .build();

                    // List에 넣기
                    pageInfoList.add(nextPageInfoDto);
                } else {
                    PageInfoDto nextPageInfoDto = PageInfoDto.builder()
                            .pageId(nextPageId)
                            .color(nextPage.getColor())
                            .template(nextPage.getTemplate())
                            .direction(nextPage.getDirection())
                            .updatedAt(nextPage.getUpdatedAt())
                            .isBookmarked(pageRepository.isLikedByPageId(userId, nextPageId))
                            .pdfUrl(nextPage.getPdfUrl())
                            .pdfPage(nextPage.getPdfPage())
                            .build();

                    // List에 넣기
                    pageInfoList.add(nextPageInfoDto);
                }

                // 페이지에 연결되어있는 다음 페이지 불러오기
                nextPage = pageRepository.getNextPageByPageId(nextPageId);
            }
            PageListResponseDto pageListResponseDto = PageListResponseDto.builder()
                                    .data(pageInfoList)
                                    .build();

            return pageListResponseDto;
        } else {
            throw new NoteNotFoundException("노트를 찾을 수 없습니다.");
        }

    }
}
