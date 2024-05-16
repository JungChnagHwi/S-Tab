package com.sixb.note.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixb.note.dto.page.PageInfoDto;
import com.sixb.note.dto.pageData.PageDataDto;
import com.sixb.note.entity.Page;
import com.sixb.note.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisDataListener {

	private final PageRepository pageRepository;

	private final RedisTemplate<String, Object> redisTemplate;

	public void messageReceived(String expiredKey) throws JsonProcessingException {
		if (!expiredKey.contains(":expired")) {
			return;
		}

		String key = expiredKey.replace(":expired", "");
		Object value = redisTemplate.opsForValue().get(key);

		if (!(value instanceof PageInfoDto)) {
			return;
		}

		PageInfoDto pageInfo = (PageInfoDto) redisTemplate.opsForValue().get(key);

		ObjectMapper mapper = new ObjectMapper();
		PageDataDto pageData = PageDataDto.builder()
				.paths(pageInfo.getPaths())
				.figures(pageInfo.getFigures())
				.images(pageInfo.getImages())
				.textBoxes(pageInfo.getTextBoxes())
				.build();
		String pageDataJson = mapper.writeValueAsString(pageData);

		Page page = Page.builder()
				.pageId(pageInfo.getPageId())
				.noteId(pageInfo.getNoteId())
				.template(pageInfo.getTemplate())
				.color(pageInfo.getColor())
				.direction(pageInfo.getDirection())
				.pdfUrl(pageInfo.getPdfUrl())
				.pdfPage(pageInfo.getPdfPage())
				.pageData(pageDataJson)
				.updatedAt(pageInfo.getUpdatedAt())
				.build();

		pageRepository.save(page);
	}

}
