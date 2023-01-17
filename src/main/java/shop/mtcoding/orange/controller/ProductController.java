package shop.mtcoding.orange.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import shop.mtcoding.orange.model.Product;
import shop.mtcoding.orange.model.ProductRepository;

@Controller
public class ProductController {

    @Autowired // Type으로 찾아냄
    private ProductRepository productRepository;

    @Autowired // session 저장소가 하나기 때문에 ioc에 저장
    private HttpSession session;

    // 싱글톤이 아닌 것들은 지정하는 것이 어렵다
    // ex> 놀러온 사람들에게 가방하나로 나눠 쓰라는 것
    // @Autowired
    // private HttpRequest request;

    @GetMapping("/test")
    private String test() {
        return "test";
    }

    @GetMapping("/redirect") // 두 개가 만들어 진다 => 다른 것
    public void redirect(HttpServletRequest request, HttpServletResponse reponse) throws IOException {
        // 헤쉬맵 (key, value)
        // HttpSession session = request.getSession(); // 세션 접근
        session.setAttribute("name", "session metacoding"); // 2번 session
        request.setAttribute("name", "metacoding"); // 1번 request
        reponse.sendRedirect("/test");
    }

    @GetMapping("/dispatcher") // 두 개가 만들어 진다 => 같은 것
    // http://localhost:8080/dispatcher 검색하면 덮어 씌워져서 그대로 변하지 않고 반영됨
    public void dispatcher(HttpServletRequest request, HttpServletResponse reponse)
            throws ServletException, IOException {
        // 헤쉬맵 (key, value)
        request.setAttribute("name", "metacoding");
        RequestDispatcher dis = request.getRequestDispatcher("/test");
        dis.forward(request, reponse);
    }

    @GetMapping({ "/", "/product" })
    public String findAll(Model model) { // model = request dispatcher
        // model에서 request dispatcher를 통해 request가 덮어 씌워짐(request 2번 생성)
        // request가 덮어 씌워짐 => 프레임워크
        List<Product> productList = productRepository.findAll(); // 오버라이딩됨
        model.addAttribute("productList", productList);
        return "product/main"; // /product 호출하면 main file이 출력
    }

    @GetMapping("/product/{id}")
    public String findOne(Model model, @PathVariable int id) { // model = request
        Product product = productRepository.findOne(id); // 오버라이딩됨
        model.addAttribute("product", product);
        return "product/detail"; // PAGE 불러온다

    }

    @GetMapping("/api/product")
    @ResponseBody
    public List<Product> apiFindAllproduct() {
        List<Product> productList = productRepository.findAll(); // 오버라이딩됨
        // file return 하기 때문에 data return을 하기위해 @ResponseBody 붙혀줌
        return productList; // /product 호출하면 data 출력
    }

    @GetMapping("/api/product/{id}")
    @ResponseBody
    public Product apiFindOneproduct(@PathVariable int id) {
        Product product = productRepository.findOne(id);
        return product;
    }

    @GetMapping("/product/addForm")
    public String addForm() {
        return "product/addForm";
    }

    @PostMapping("/product/add")
    private String add(String name, int price, int qty) {// String price, String qty 가능
        int result = productRepository.insert(name, price, qty); // alt+enter => method추가
        // 1이 아니면 다 실패
        if (result == 1) { // xml
            // return "main"만 하면 값을 들고 가지 못해서 redirect 해줘야함
            return "redirect:/product";
        } else {
            return "redirect:/product/addForm";
        }
    }

    @PostMapping("/product/{id}/delete")
    private String delete(@PathVariable int id) {
        int result = productRepository.delete(id);
        if (result == 1) {
            return "redirect:/product";
        } else {
            return "redirect:/product/" + id;
        }
    }

    @GetMapping("/product/{id}/updateForm")
    private String updateForm(@PathVariable int id, Model model) {
        Product product = productRepository.findOne(id);
        model.addAttribute("product", product);
        return "product/updateForm";
    }

    @PostMapping("/product/{id}/update")
    public String update(@PathVariable int id, String name, int price, int qty) {
        // 레파지토리 update 메서드 호출
        // 결과 받기(1,-1)
        // 성공 -> 상세보기 페이지("/product/{id}")
        // 실패 -> 수정 페이지("/product/{id}/updateForm")
        int result = productRepository.update(id, name, price, qty);
        if (result == 1) {
            return "redirect:/product/" + id;
        } else {
            return "redirect:/product/" + id + "/updateForm";
        }
    }
}
