package ${package}.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ${package}.dao.${className}DAO;
import ${package}.domain.${className};
import org.yangpeng.openplatform.service.${className}Service;
import org.yangpeng.openplatform.util.Page;

@Service("${className}Service")
public class ${className}ServiceImpl implements ${className}Service {
	@Resource
	private ${className}DAO ${classNameLower}DAO;

	@Override
	public List<${className}> queryList(${className} ${classNameLower},Page<${className}> page) {
		return this.${classNameLower}DAO.queryList(${classNameLower},page);
	}

	@Override
	public ${className} findById(Integer id) {
		return this.${classNameLower}DAO.queryById(id);
	}

	@Override
	public Integer save(${className} ${classNameLower}) {
		if( ${classNameLower}.getId() != null ){
			this.${classNameLower}DAO.update(${classNameLower});
		}else{
			this.${classNameLower}DAO.add(${classNameLower});
		}
		return ${classNameLower}.getId();
	}

	@Override
	public void delete(List<String> codes) {
		this.${classNameLower}DAO.delete(codes);
	}

}