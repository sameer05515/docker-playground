package com.example.todo.service;
import com.example.todo.cache.TodoCache;
import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class TodoService {
 private final TodoRepository repo; private final TodoCache cache;
 public TodoService(TodoRepository repo,TodoCache cache){this.repo=repo;this.cache=cache;}

 @Transactional public TodoResponse create(TodoRequest r){
  Todo t=repo.save(Todo.builder().title(r.title()).description(r.description()).completed(Boolean.TRUE.equals(r.completed())).build());
  TodoResponse x=toResponse(t); cache.put(x); return x;
 }

 @Transactional(readOnly=true) public TodoResponse findById(Long id){
  TodoResponse cached=cache.get(id);
  if(cached!=null) return cached;
  Todo t=repo.findById(id).orElseThrow(()->new TodoNotFoundException(id));
  TodoResponse x=toResponse(t); cache.put(x); return x;
 }

 @Transactional(readOnly=true) public PageResponse<TodoResponse> search(String keyword,Boolean completed,int page,int size){
  page=Math.max(0,page); size=Math.min(Math.max(1,size),100);
  Page<TodoResponse> p=repo.search(keyword==null||keyword.isBlank()?null:keyword.trim(),completed,
    PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"id"))).map(this::toResponse);
  return new PageResponse<>(p.getContent(),p.getNumber(),p.getSize(),p.getTotalElements(),p.getTotalPages(),p.isFirst(),p.isLast());
 }

 @Transactional public TodoResponse update(Long id,TodoRequest r){
  Todo t=repo.findById(id).orElseThrow(()->new TodoNotFoundException(id));
  t.setTitle(r.title()); t.setDescription(r.description()); t.setCompleted(Boolean.TRUE.equals(r.completed()));
  TodoResponse x=toResponse(repo.save(t)); cache.put(x); return x;
 }

 @Transactional public void delete(Long id){
  Todo t=repo.findById(id).orElseThrow(()->new TodoNotFoundException(id));
  repo.delete(t); cache.evict(id);
 }
 public long cacheTtl(Long id){return cache.ttl(id);}
 public void clearCache(){cache.clear();}
 private TodoResponse toResponse(Todo t){return new TodoResponse(t.getId(),t.getTitle(),t.getDescription(),t.isCompleted());}
}