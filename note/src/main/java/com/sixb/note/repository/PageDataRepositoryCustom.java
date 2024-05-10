package com.sixb.note.repository;

import com.sixb.note.entity.PageData;

public interface PageDataRepositoryCustom {
    PageData findDataById(String id);
}
