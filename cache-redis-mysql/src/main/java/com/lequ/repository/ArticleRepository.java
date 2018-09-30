package com.lequ.repository;

import org.springframework.data.repository.CrudRepository;

import com.lequ.entity.Article;

public interface ArticleRepository extends CrudRepository<Article, Long>  {
}
