package com.sixb.note.api.service;

import com.sixb.note.dto.page.*;
import com.sixb.note.dto.pageData.FigureDto;
import com.sixb.note.dto.pageData.ImageDto;
import com.sixb.note.dto.pageData.PathDto;
import com.sixb.note.dto.pageData.TextBoxDto;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final PageDataRepository pageDataRepository;
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
                    .build();

            // db에 저장하고 반환
            pageRepository.save(newPage);
            pageRepository.save(beforePage);

            // mongodb에 데이터 만들기
            // 필기 데이터 저장
            PageData pageData = PageData.builder()
                    .id(pageId)
                    .build();

            pageDataRepository.save(pageData);

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
                // 검색 잘 되는지 나중에 확인해야함
                Optional<PageData> optionalPageData = pageDataRepository.findById(pageId);
                List<FigureDto> figures = request.getFigures();
                List<ImageDto> images = request.getImages();
                List<TextBoxDto> textBoxes = request.getTextBoxes();
                List<PathDto> paths = request.getPaths();
//                List<PathDto> paths = Optional.ofNullable(request.getPaths());

                System.out.println("fig: "+figures);
                if (optionalPageData.isPresent()) {
                    PageData pageData = optionalPageData.get();

                    pageData.setFigures(figures);
                    pageData.setImages(images);
                    pageData.setTextBoxes(textBoxes);
//                    pageData.setPaths(optionalPaths.orElse(null));

                    pageDataRepository.save(pageData);
                } else {
                    PageData pageData = PageData.builder()
                            .id(pageId)
//                            .paths(optionalPaths.orElse(null))
                            .images(images)
                            .figures(figures)
                            .textBoxes(textBoxes)
                            .build();
                    pageDataRepository.save(pageData);
                }

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
    public PageListResponseDto getPageList(long userId, String noteId) throws NoteNotFoundException, PageNotFoundException {
        Note note = noteRepository.findNoteById(noteId);
        if (note != null) {
            List<PageInfoDto> pageInfoList = new ArrayList<>();

            // 일단 구현 후 하위부분 do-while이나 while로 바꿀 예정

            // noteId에 연결되어있는 페이지 불러오기
            Page firstPage = pageRepository.findFirstPageByNoteId(noteId);
            String fistPageId = firstPage.getId();
            if (firstPage.getIsDeleted() == false) {
                // 그 페이지에 해당하는 data 불러오기
                Optional<PageData> optionalFirstPageData = pageDataRepository.findById(firstPage.getId());

                if (!optionalFirstPageData.isPresent()) {
                    throw new PageNotFoundException("페이지 데이터를 찾을 수 없습니다.");
                }
                PageData firstPageData = optionalFirstPageData.get();
                // optional로 nullException 처리해주기
                Optional<String> pdfUrl = Optional.ofNullable(firstPage.getPdfUrl());
                Optional<Integer> pdfPage = Optional.ofNullable(firstPage.getPdfPage());
                Optional<List<PathDto>> paths = Optional.ofNullable(firstPageData.getPaths());
                Optional<List<FigureDto>> figures = Optional.ofNullable(firstPageData.getFigures());
                Optional<List<ImageDto>> images = Optional.ofNullable(firstPageData.getImages());
                Optional<List<TextBoxDto>> textBoxes = Optional.ofNullable(firstPageData.getTextBoxes());

                // dto 빌드
                PageInfoDto pageInfoDto = PageInfoDto.builder()
                        .pageId(fistPageId)
                        .color(firstPage.getColor())
                        .template(firstPage.getTemplate())
                        .direction(firstPage.getDirection())
                        .updatedAt(firstPage.getUpdatedAt())
                        .isBookmarked(pageRepository.isLikedByPageId(userId, fistPageId))
                        .pdfUrl(pdfUrl.orElse(null))
                        .pdfPage(pdfPage.orElse(null))
                        .paths(paths.orElse(null))
                        .figures(figures.orElse(null))
                        .textBoxes(textBoxes.orElse(null))
                        .images(images.orElse(null))
                        .build();
                // List에 넣기
                pageInfoList.add(pageInfoDto);
            }

            Page nextPage = pageRepository.getNextPageByPageId(fistPageId);

            while (nextPage != null) { // 다음 페이지가 없을때까지
                // 그 페이지에 해당하는 data 불러오기
                String nextPageId = nextPage.getId();

                if (nextPage.getIsDeleted() == false) {
                    Optional<PageData> optionalNextData = pageDataRepository.findById(nextPageId);
                    if (!optionalNextData.isPresent()) {
                        throw new PageNotFoundException("페이지 데이터를 찾을 수 없습니다.");
                    }
                    PageData nextData = optionalNextData.get();
                    // optional로 nullException 처리해주기
                    Optional<String> pdfUrl = Optional.ofNullable(nextPage.getPdfUrl());
                    Optional<Integer> pdfPage = Optional.ofNullable(nextPage.getPdfPage());
                    Optional<List<PathDto>> paths = Optional.ofNullable(nextData.getPaths());
                    Optional<List<FigureDto>> figures = Optional.ofNullable(nextData.getFigures());
                    Optional<List<ImageDto>> images = Optional.ofNullable(nextData.getImages());
                    Optional<List<TextBoxDto>> textBoxes = Optional.ofNullable(nextData.getTextBoxes());

                    // dto 빌드
                    PageInfoDto nextPageInfoDto = PageInfoDto.builder()
                            .pageId(nextPageId)
                            .color(nextPage.getColor())
                            .template(nextPage.getTemplate())
                            .direction(nextPage.getDirection())
                            .updatedAt(nextPage.getUpdatedAt())
                            .isBookmarked(pageRepository.isLikedByPageId(userId, fistPageId))
                            .pdfUrl(pdfUrl.orElse(null))
                            .pdfPage(pdfPage.orElse(null))
                            .paths(paths.orElse(null))
                            .figures(figures.orElse(null))
                            .textBoxes(textBoxes.orElse(null))
                            .images(images.orElse(null))
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
