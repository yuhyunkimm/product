package shop.mtcoding.orange.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductRepository { // ProductRepository inpl 되면서 ioc에 올라간다
    public List<Product> findAll(); // findAll = select id 값

    public Product findOne(int id);

    // -1 DB에러, 1 변경된 행이 1건, 0 변경된 행이 없다
    // 변경된 행이 return
    // public int insert(String name, int price, int qty);
    public int insert(@Param("name") String name, @Param("price") int price, @Param("qty") int qty);

    public int delete(@Param("id") int id);

    public int update(@Param("id") int id, @Param("name") String name, @Param("price") int price,
            @Param("qty") int qty);
}
