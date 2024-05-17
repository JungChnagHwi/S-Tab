package com.sixb.note.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixb.note.dto.page.*;
import com.sixb.note.dto.pageData.PageDataDto;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.exception.PageNotFoundException;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.util.Const;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PageService {

	private final PageRepository pageRepository;
	private final NoteRepository noteRepository;

	private final RedisTemplate<String, PageInfoDto> redisTemplate;

	public PageCreateResponseDto createPage(PageCreateRequestDto request) throws PageNotFoundException, JsonProcessingException {
		String beforePageId = request.getBeforePageId();

		Page beforePage = pageRepository.findPageById(beforePageId)
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		if (beforePage.getIsDeleted()) {
			throw new PageNotFoundException("삭제된 페이지 입니다.");
		}

		Page newPage = createNewPage(beforePage);

		// 앞페이지에 이어진 페이지 찾기
		Page connectPage = pageRepository.getNextPageByPageId(beforePageId);

		// 페이지가 있다면
		if (connectPage != null) {
			// 그 페이지와 새로운 페이지 연결
			newPage.setNextPage(connectPage);
			// 앞페이지와 연결 삭제
			pageRepository.deleteNextPageRelation(beforePageId);
		}

		// 페이지 링크하기
		beforePage.setNextPage(newPage);

		// responsedto에 넣기
		PageCreateResponseDto responseDto = PageCreateResponseDto.builder()
				.pageId(newPage.getPageId())
				.color(newPage.getColor())
				.template(newPage.getTemplate())
				.direction(newPage.getDirection())
				.updatedAt(newPage.getUpdatedAt())
				.build();

		// db에 저장하고 반환
		pageRepository.save(newPage);
		pageRepository.save(beforePage);

		return responseDto;
	}

	public void deletePage(String pageId) throws PageNotFoundException {
		Page page = pageRepository.findPageById(pageId)
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		if (page.getIsDeleted()) {
			throw new PageNotFoundException("이미 삭제된 페이지입니다.");
		}

		page.setIsDeleted(true);
		page.setUpdatedAt(LocalDateTime.now());
		pageRepository.save(page);
	}

	// 데이터 저장
	public void saveData(SaveDataRequestDto request) throws JsonProcessingException, NoteNotFoundException, PageNotFoundException {
		String pageId = request.getPageId();
		Page page = pageRepository.findPageById(pageId)
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		Note note = noteRepository.findNoteById(page.getNoteId())
				.orElseThrow(() -> new NoteNotFoundException("노트를 찾을 수 없습니다."));

		if (page.getIsDeleted()) {
			throw new PageNotFoundException("이미 삭제된 페이지입니다.");
		}

		LocalDateTime now = LocalDateTime.now();

		PageDataDto pageData = request.getPageData();

		ObjectMapper mapper = new ObjectMapper();

		String pageDataString = mapper.writeValueAsString(pageData);

		page.setUpdatedAt(now);
		page.setPageData(pageDataString);

		note.setUpdatedAt(now);

		noteRepository.save(note);

		setPageInfoDto(page);
	}

	public PageUpdateDto updatePage(PageUpdateDto request) throws PageNotFoundException, JsonProcessingException {
		String pageId = request.getPageId();
		Page page = pageRepository.findPageById(pageId)
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		if (page.getIsDeleted()) {
			throw new PageNotFoundException("이미 삭제된 페이지입니다.");
		}

		// 양식 정보 수정
		page.setTemplate(request.getTemplate());
		page.setColor(request.getColor());
		page.setDirection(request.getDirection());

		setPageInfoDto(page);

		return request;
	}

	// 페이지 조회
	public PageListResponseDto getPageList(String noteId, long userId) throws NoteNotFoundException, JsonProcessingException {
		Note note = noteRepository.findNoteById(noteId)
				.orElseThrow(() -> new NoteNotFoundException("노트를 찾을 수 없습니다."));

		List<PageInfoDto> pageInfoList = new ArrayList<>();

		// noteId에 연결되어있는 페이지 모두 불러오기
		List<Page> pageList = pageRepository.findAllPagesByNoteId(noteId);

		for (Page page : pageList) {
			PageInfoDto pageInfoDto = getPageInfoDto(page);
			pageInfoDto.setIsBookmarked(pageRepository.isLikedByPageId(userId, page.getPageId()));
			// pageInfoList에 넣기
			pageInfoList.add(pageInfoDto);
		}

		return PageListResponseDto.builder()
				.data(pageInfoList)
				.title(note.getTitle())
				.build();
	}

	// 페이지 링크 - 보류
//    public void linkPage(PageLinkRequestDto request) throws PageNotFoundException {
//        Page linkPage = pageRepository.findPageById(request.getLinkPageId());
//        Page targetPage = pageRepository.findPageById(request.getTargetPageId());
//    }

	public PageInfoDto copyPage(PageCopyRequestDto request) throws JsonProcessingException, PageNotFoundException, NoteNotFoundException {
		String beforePageId = request.getBeforePageId();

		Page beforePage = pageRepository.findPageById(beforePageId)
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		Page targetPage = pageRepository.findPageById(request.getTargetPageId())
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		noteRepository.findNoteById(beforePage.getNoteId())
				.orElseThrow(() -> new NoteNotFoundException("존재하지 않는 노트입니다."));

		LocalDateTime now = LocalDateTime.now();

		// before 페이지에 이어서 페이지 만들기
		Page newPage = Page.builder()
				.pageId(IdCreator.create("p"))
				.noteId(beforePage.getNoteId())
				.template(targetPage.getTemplate())
				.color(targetPage.getColor())
				.direction(targetPage.getDirection())
				.pdfUrl(targetPage.getPdfUrl())
				.pdfPage(targetPage.getPdfPage())
				.pageData(targetPage.getPageData())
				.createdAt(now)
				.updatedAt(now)
				.build();

		// 이전페이지에 이어진 페이지 찾기
		Page connectPage = pageRepository.getNextPageByPageId(beforePageId);

		// 페이지가 있다면
		if (connectPage != null) {
			// 그 페이지와 새로운 페이지 연결
			newPage.setNextPage(connectPage);
			// 앞페이지와 연결 삭제
			pageRepository.deleteNextPageRelation(beforePageId);
		}

		// 페이지 링크하기
		beforePage.setNextPage(newPage);

		// responsedto에 넣기
		PageInfoDto response = getPageInfoDto(newPage);
		response.setIsBookmarked(false);

		// db에 저장하고 반환
		pageRepository.save(newPage);
		pageRepository.save(beforePage);

		return response;
	}

	// pdf 가져오기
	public List<PageInfoDto> pdfPage(PagePdfRequestDto request) throws PageNotFoundException, JsonProcessingException {
		String beforePageId = request.getBeforePageId();
		Page beforePage = pageRepository.findPageById(beforePageId)
				.orElseThrow(() -> new PageNotFoundException("존재하지 않는 페이지입니다."));

		if (beforePage.getIsDeleted()) {
			throw new PageNotFoundException("이미 삭제된 페이지입니다.");
		}

		String pdfUrl = request.getPdfUrl();
		int pdfPageCount = request.getPdfPageCount();

		// 앞페이지에 이어진 페이지 찾기
		Page connectPage = pageRepository.getNextPageByPageId(beforePageId);

		// 페이지가 있다면
		if (connectPage != null) {
			// 앞페이지와 연결 삭제
			pageRepository.deleteNextPageRelation(beforePageId);
		}

		List<PageInfoDto> response = new ArrayList<>();

		// pdfcount 만큼 for문 돌면서 페이지 생성하기
		for (int i = pdfPageCount; i > 0; i--) {

			Page page = createNewPage(beforePage);
			// 추가 정보 저장
			page.setTemplate("blank");
			page.setColor("white");
			page.setPdfPage(i);
			page.setPdfUrl(pdfUrl);

			// 페이지 링크하기
			if (connectPage != null) {
				page.setNextPage(connectPage);
			}
			pageRepository.save(page);

			PageInfoDto pageInfoDto = getPageInfoDto(page);
			pageInfoDto.setIsBookmarked(false);

			response.add(0, pageInfoDto);

			connectPage = page;
		}

		// before페이지 링크하기
		beforePage.setNextPage(connectPage);

		// 페이지 저장
		pageRepository.save(beforePage);

		return response;
	}

	private Page createNewPage(Page beforePage) {
		LocalDateTime now = LocalDateTime.now();

		return Page.builder()
				.pageId(IdCreator.create("p"))
				.color(beforePage.getColor())
				.template(beforePage.getTemplate())
				.direction(beforePage.getDirection())
				.noteId(beforePage.getNoteId())
				.pageData(Const.INIT_PAGE_DATA)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}


	//    @Cacheable(value = "page", key = "#page.pageId", cacheManager = "redisCacheManager")
	private PageInfoDto getPageInfoDto(Page page) throws JsonProcessingException {
		String redisKey = "page:" + page.getPageId();

		PageInfoDto pageInfo = redisTemplate.opsForValue().get(redisKey);

		if (pageInfo != null) {
			return pageInfo;
		}

		ObjectMapper mapper = new ObjectMapper();
		PageDataDto pageDataDto = mapper.readValue(page.getPageData(), PageDataDto.class);

		pageInfo = PageInfoDto.builder()
				.pageId(page.getPageId())
				.noteId(page.getNoteId())
				.color(page.getColor())
				.template(page.getTemplate())
				.direction(page.getDirection())
				.pdfPage(page.getPdfPage())
				.pdfUrl(page.getPdfUrl())
				.createdAt(page.getCreatedAt().toString())
				.updatedAt(page.getUpdatedAt().toString())
				.paths(pageDataDto.getPaths())
				.figures(pageDataDto.getFigures())
				.images(pageDataDto.getImages())
				.textBoxes(pageDataDto.getTextBoxes())
				.build();

		redisTemplate.opsForValue().set(redisKey, pageInfo);

		String redisExpireKey = redisKey + ":expired";
		redisTemplate.opsForValue().set(redisExpireKey, pageInfo, Const.PAGE_CACHE_EXPIRE_TIME);

		return pageInfo;
	}

	//    @CachePut(value = "page", key = "#page.pageId", cacheManager = "redisCacheManager")
	private void setPageInfoDto(Page page) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		PageDataDto pageDataDto = mapper.readValue(page.getPageData(), PageDataDto.class);

		PageInfoDto pageInfo = PageInfoDto.builder()
				.pageId(page.getPageId())
				.noteId(page.getNoteId())
				.color(page.getColor())
				.template(page.getTemplate())
				.direction(page.getDirection())
				.pdfPage(page.getPdfPage())
				.pdfUrl(page.getPdfUrl())
				.createdAt(page.getCreatedAt().toString())
				.updatedAt(page.getUpdatedAt().toString())
				.paths(pageDataDto.getPaths())
				.figures(pageDataDto.getFigures())
				.images(pageDataDto.getImages())
				.textBoxes(pageDataDto.getTextBoxes())
				.build();

		String redisKey = "page:" + page.getPageId();
		redisTemplate.opsForValue().set(redisKey, pageInfo);

		String redisExpireKey = redisKey + ":expired";
		redisTemplate.opsForValue().set(redisExpireKey, pageInfo, Const.PAGE_CACHE_EXPIRE_TIME);
	}

}
