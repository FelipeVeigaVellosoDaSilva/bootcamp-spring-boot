package com.felipeveiga.fvcatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.felipeveiga.fvcatalog.entities.Category;
import com.felipeveiga.fvcatalog.entities.dto.CategoryDTO;
import com.felipeveiga.fvcatalog.repositories.CategoryRepository;
import com.felipeveiga.fvcatalog.services.exceptions.DatabaseException;
import com.felipeveiga.fvcatalog.services.exceptions.ObjectNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repo;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAll(Pageable pageRequest){
		Page<Category> list = repo.findAll(pageRequest);
		return list.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repo.findById(id);
		Category entity = obj.orElseThrow(() -> new ObjectNotFoundException("Id not exist: " + id));
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repo.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity = repo.getOne(id);
			entity.setName(dto.getName());
			entity = repo.save(entity);
			return new CategoryDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ObjectNotFoundException("Id not exist: " + id);
		}
		
	}
	
	@Transactional
	public void delete(Long id) {
		try {
			repo.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ObjectNotFoundException("Id not exist: " + id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
		
	}
	
}
