package ${package}.service;

import java.util.List;
import java.util.Map;

import org.yangpeng.openplatform.util.Page;
import ${package}.domain.${className};
import org.yangpeng.openplatform.util.DataSource;

public interface ${className}Service {

	@DataSource("slave")
	public List<${className}> queryList(${className} ${classNameLower} ,Page<${className}> page);
	
	@DataSource("slave")
	public ${className} findById(Integer id);
	
	@DataSource("master")
	public Integer save(${className} ${classNameLower});
	
	@DataSource("master")
	public void delete(List<String> codes);
	
}
