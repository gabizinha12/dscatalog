
package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService { // camada de serviço

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> list = repository.findAll(pageable); // Busca todos os objetos da lista Category
		return list.map(x -> new CategoryDTO(x)); // Converte os objetos da lista Category para lista CategoryDto

	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id); // busca da tabela o objeto, ele vem como Optional
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException(" Id não existe")); // pega o objeto que tá no Optional e converte pra Category, caso não exista lança uma exceção
		return new CategoryDTO(entity); // converte o objeto entity para CategoryDTO
	}

	@Transactional(readOnly = true)
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category(); // cria variavel do tipo Category
		entity.setName(dto.getName()); // atribui o valor no campo da tabela name
		entity = repository.save(entity); // transfere para a classe Reposity e salva no banco e retorna a entidade
		return new CategoryDTO(entity); // retorna a entidade do tipo CategoryDTO para o método
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		try {
			Category entity = repository.getOne(id); // cria a entidade do tipo Category e busca o id do tipo
														// CAtegoryDTO passado como parametro
			entity.setName(dto.getName()); // atualiza o campona tabela
			entity = repository.save(entity); // salva no banco
			return new CategoryDTO(entity); // retorna uma nova entidade atualizada do tipo CategoryDTO
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado " + id);
		}
	}

	public void delete(Long id) {
		try{
		repository.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("id não existe " + id);
		}catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade no banco");
		}
	}
}