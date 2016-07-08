package ${package}.domain;

public class ${className}Bean  implements Serializable {

<#list table.columns as column>
	private ${column.type} ${column.name};//${column.label}
</#list>
	
// setter and getter
<#list table.columns as column>
	public ${column.type} get${column.nameUpper}(){
		return ${column.name};
	}
	
	public void set${column.nameUpper}(${column.type} ${column.name}){
		this.${column.name} = ${column.name};
	}
</#list>
}
