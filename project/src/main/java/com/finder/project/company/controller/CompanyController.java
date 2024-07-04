package com.finder.project.company.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.finder.project.company.dto.Company;
import com.finder.project.company.dto.CompanyDetail;
import com.finder.project.company.dto.Credit;
import com.finder.project.company.dto.IntroCom;
import com.finder.project.company.dto.Order;
import com.finder.project.company.dto.OrderCreditDTO;
import com.finder.project.company.dto.PasswordConfirmRequest;
import com.finder.project.company.dto.Product;
import com.finder.project.company.service.CompanyService;
import com.finder.project.main.dto.Page;
import com.finder.project.recruit.service.RecruitService;
import com.finder.project.resume.dto.Resume;
import com.finder.project.user.dto.Users;
import com.finder.project.user.service.UserService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder; 

    @Autowired
    RecruitService recruitService;

    /*     // introduce_com 화면 (기업소개)
    // 조회는 세션에서 해주고 있다. (Users에서 Company CompanyDetail 받아옴)
    @GetMapping("/introduce_com")
    public String introduce_com(@AuthenticationPrincipal CustomUser customUser) throws Exception {

        return "/company/introduce_com";
    } */


    // introduce_com 화면 (기업소개)
    // 조회는 세션에서 해주고 있다. (Users에서 Company CompanyDetail 받아옴)
    @GetMapping("/introduce_com")
    public ResponseEntity<?> introduce_com(@RequestParam("userNo") int userNo) throws Exception {
        Company company = companyService.selectByUserNo(userNo);


        try {
            CompanyDetail ComDetail = companyService.selectCompanyDetailByComNo(company.getComNo());
            
            if (ComDetail != null) {
                log.info("기업 정보 가지고 오기" + ComDetail);
            }

            Map<String , Object> response = new HashMap<>();
            response.put("company", company);
            response.put("comDetail", ComDetail);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 기업 상세 정보 등록 (기업소개)
    @PostMapping("/introduceInsert_com")
    public ResponseEntity<?> introduceComInsertPro(
            @RequestParam("userNo") int userNo,
            @ModelAttribute IntroCom introCom) {
        try {
            log.info("introduceInsert  : : : : : : : :  : :: : : user" + userNo);
            log.info("introCom  : : : : : : : :  : :: : : introCom" + introCom);
            
            // IntroCom 객체를 Company, CompanyDetail 객체로 변환
            Company company = companyService.selectByUserNo(userNo);
            CompanyDetail companyDetail = new CompanyDetail();
            
            log.info("company  : : : : : : : :  : :: : : company" + company); // 잘 불러옴
            log.info("companyDetail  : : : : : : : :  : :: : : companyDetail" + companyDetail); // null 뜸
            
            
            companyDetail.setComBirth(introCom.getComBirth());
            companyDetail.setComEmpCount(introCom.getComEmpCount());
            companyDetail.setComSales(introCom.getComSales());
            companyDetail.setComSize(introCom.getComSize());
            companyDetail.setComRepresent(introCom.getComRepresent());
            companyDetail.setComContent(introCom.getComContent());
            companyDetail.setComNo(company.getComNo());
            
            log.info("----------------------------여기까지는 옴" + companyDetail);

            // 데이터 삽입 요청
            // int result = companyService.insertCompany(company);
            int result2 = companyService.insertCompanyDetail(companyDetail);
            
            log.info("여기까지 오나 확인" + result2 );

            if (result2 > 0) {
                // 성공적으로 데이터 삽입됨
                // Map<String, Object> response = new HashMap<>();
                // response.put("company", company);
                // response.put("comDetail", companyDetail);

                log.info("정보 들어갔다 ~~~~~~~~~~~~~~~~~~~~~~~~~~" + result2);
                return new ResponseEntity<>("success insert comDetail", HttpStatus.OK);
            } else {
                // 삽입 실패
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    
        } catch (Exception e) {
            // 예외 발생 시
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    



    
    @PutMapping("/update_detail")
    public ResponseEntity<?> introduce_com_updatePro(@RequestParam("userNo") int userNo, @Validated @RequestBody IntroCom introCom) {
        try {
            Company company = companyService.selectByUserNo(userNo);
            if (company == null) {
                return new ResponseEntity<>("Company not found", HttpStatus.NOT_FOUND);
            }

            CompanyDetail comDetail = companyService.selectCompanyDetailByComNo(company.getComNo());
            if (comDetail == null) {
                return new ResponseEntity<>("CompanyDetail not found", HttpStatus.NOT_FOUND);
            }

            // IntroCom에서 데이터를 추출하여 객체에 설정
            company.setComName(introCom.getComName());

            comDetail.setComBirth(introCom.getComBirth());
            comDetail.setComEmpCount(introCom.getComEmpCount());
            comDetail.setComSales(introCom.getComSales());
            comDetail.setComSize(introCom.getComSize());
            comDetail.setComRepresent(introCom.getComRepresent());
            comDetail.setComContent(introCom.getComContent());
            comDetail.setComNo(company.getComNo());

            // 데이터 업데이트
            int result = companyService.updateCompanyDetail(comDetail);
            int result2 = companyService.updateCompany(company);

            // 처리 결과 반환
            if (result > 0 && result2 > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("company", company);
                response.put("comDetail", comDetail);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Update failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid number format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    // 개인 정보 수정
    @PostMapping("/update_info")
    public ResponseEntity<Boolean> updateCompany(@RequestBody Users request)  throws Exception {

        log.info("클라이언트에서 받는 개인정보" + request);
                            
        
        int userNo = request.getUserNo();

        Users userInfo = userService.selectByUserNo(userNo);

        // 세션에서 사용자 정보 가져오기
        
        if (userInfo == null) {
            // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
            return  ResponseEntity.ok(false);
        }

        // 사용자 정보 업데이트
        userInfo.setUserBirth(request.getUserBirth());
        userInfo.setUserPhone(request.getUserPhone());
        userInfo.setUserEmail(request.getUserEmail());
        userInfo.setUserGender(request.getUserGender());

        
        // company = companyService.selectByUserNo(user.getUserNo());
        
        // company = user.getCompany();
        // company.setComAddress(comAddress); // 기업 주소 업데이트
        

        // 데이터 요청
        int result = companyService.updateUserInfo(userInfo);
        
        // 데이터 처리 성공 
        if( result > 0 ) {
            log.info("User : " + userInfo.getUserBirth());
            // log.info("Company : " + company.getComAddress());
            // user.setCompany(company);
            // session.setAttribute("user", user);
            return  ResponseEntity.ok(true);
        }
        // 데이터 처리 실패
        return  ResponseEntity.ok(false);
    }

    // kakao 로그인하면 여길루옴
    @PostMapping("/update_com_kakaoInfo")
    public String updateKakao(HttpSession session, Company company
                            ,@RequestParam("userName") String userName
                            ,@RequestParam("userBirth") String userBirth
                            ,@RequestParam("userPhone") String userPhone
                            ,@RequestParam("userEmail") String userEmail
                            ) throws Exception {
        
        // 세션에서 사용자 정보 가져오기
        Users user = (Users) session.getAttribute("user");
        
        if (user == null) {
            // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 사용자 정보 업데이트
        user.setUserBirth(userBirth);
        user.setUserPhone(userPhone);
        user.setUserEmail(userEmail);

        
        // company = companyService.selectByUserNo(user.getUserNo());
        
        // company = user.getCompany();
        // company.setComAddress(comAddress); // 기업 주소 업데이트
        

        // 데이터 요청
        int result = companyService.updateUserInfo(user);
        
        // 데이터 처리 성공 
        if( result > 0 ) {
            log.info("User : " + user.getUserBirth());
            // log.info("Company : " + company.getComAddress());
            // user.setCompany(company);
            // session.setAttribute("user", user);
            return "redirect:/user/social_user";
        }
        // 데이터 처리 실패
        return "redirect:/user/error";
    }

    // 비밀번호 확인 (update_user)
    // @PostMapping("/update_pw_confirm")
    // public ResponseEntity<Boolean> pw_confirm(@RequestBody PasswordConfirmRequest request, HttpSession session) {
        
    //     // 세션에서 사용자 정보 가져오기
    //     Users user = (Users) session.getAttribute("user");
    //     // 현재 비밀번호를 암호화해서, 세션에 암호화된 비밀번호와 비교 (맞으면 1)
    //     boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getUserPw());
    //     return ResponseEntity.ok(isMatch);
    // }
    @PostMapping("/update_pw_confirm")
    public ResponseEntity<Boolean> pw_confirm(@RequestBody PasswordConfirmRequest request) throws Exception{
        log.info("request의 값이 뭐가들어가있는지 " + request);
        // 세션에서 사용자 정보 가져오기
        Users user = userService.selectByUserNo(request.getUserNo());
        // 현재 비밀번호를 암호화해서, 세션에 암호화된 비밀번호와 비교 (맞으면 1)
        boolean isMatch = passwordEncoder.matches(request.getUserPw(), user.getUserPw());
        log.info("isMatch의 값이 뭐가들어가있는지 " + isMatch);
        return ResponseEntity.ok(isMatch);
    }

    // 비밀번호 수정 (update_user)
    // @PostMapping("/update_pw")
    // public String updateCompany(HttpSession session 
    //                             ,@RequestParam("userPw") String userPw
    //                             //,@RequestParam("userBeforePw") String userBeforePw
    //                             ) throws Exception {
        
    //     // 세션에서 사용자 정보 가져오기
    //     Users user = (Users) session.getAttribute("user");
        
    //     if (user == null) {
    //         // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
    //         return "redirect:/login";
    //     }
        
    //     user.setUserPw(userPw);
    //     // user.setUserBeforePw(userBeforePw);

    //     String password = user.getUserPw();
    //     String encodedPassword = passwordEncoder.encode(password);  // 🔒 비밀번호 암호화
    //     user.setUserPw(encodedPassword);

    //     // String beforePassword = user.getUserBeforePw();
    //     // String encodedBeforePassword = passwordEncoder.encode(beforePassword);  // 🔒 비밀번호 암호화
    //     // user.setUserBeforePw(encodedBeforePassword);

        
    //     // 데이터 요청
    //     int result = companyService.updateUserPw(user);


    //     // 데이터 처리 성공 
    //     if( result > 0 ) {
    //         session.setAttribute("user", user);
    //         return "redirect:/user/update_user";
    //     }
    //     // 데이터 처리 실패
    //     return "redirect:/user/error";
    // }

    // 비밀번호 수정 (update_user)updateCompany
    @PostMapping("/update_pw")
    public ResponseEntity<Boolean> updateCompany(@RequestBody PasswordConfirmRequest request) throws Exception{

        log.info("앙오ㅜㅇ아:::::" + request);
        
        // 세션에서 사용자 정보 가져오기
        Users user = userService.selectByUserNo(request.getUserNo());
        
        if (user == null) {
            // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
            return  ResponseEntity.ok(false);
        }
        String userPw = request.getUserPw();
        
        user.setUserPw(userPw);
        // user.setUserBeforePw(userBeforePw);

        String password = user.getUserPw();
        String encodedPassword = passwordEncoder.encode(password);  // 🔒 비밀번호 암호화
        user.setUserPw(encodedPassword);

        // String beforePassword = user.getUserBeforePw();
        // String encodedBeforePassword = passwordEncoder.encode(beforePassword);  // 🔒 비밀번호 암호화
        // user.setUserBeforePw(encodedBeforePassword);

        log.info("앙오ㅜㅇ아:::::" + encodedPassword);
        // 데이터 요청
        int result = companyService.updateUserPw(user);


        // 데이터 처리 성공 
        if( result > 0 ) {
            log.info("데이터 처리 성공");
            return  ResponseEntity.ok(true);
        }
        // 데이터 처리 실패
        return  ResponseEntity.ok(false);
    }
    



//-------결제------------------------------------------------------------------------


    // 결제상품 화면 [GET]
    // 상품 3개 있는 화면인데 Link to 에서 하드코딩된 productNo=1,2,3 만 넘기면 된다. ⭕⭕⭕
    // 따로 화면 그릴 때 요청할 데이터가 없다. 
    // @GetMapping("/credit/credit_com")
    // public String credit_com() throws Exception {
    //     return "/company/credit/credit_com";
    // }





    // 결제상품 세부 화면 [GET]
    // @GetMapping("/credit/credit_detail_com")
    // public String credit_detail_com(@RequestParam("productNo") int productNo, Model model, Product product) throws Exception {

    //     product.setProductNo(productNo);
    //     product = companyService.selectProduct(productNo);

    //     model.addAttribute("product", product);
    //     return "company/credit/credit_detail_com";
    // }

    // 결제상품 세부 화면 [GET]
    // 일단 데이터 찍힘 ⭕⭕⭕
    @GetMapping("credit/credit_detail_com")
    public ResponseEntity<?> credit_detail_com(@RequestParam("productNo") Integer productNo
                                             , @RequestParam("userNo") Integer userNo) {
        try {
            Product product = companyService.selectProduct(productNo);
            Users user = userService.selectByUserNo(userNo);

            if (product == null || user == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("user", user);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    // 토스 페이먼츠 메인 [GET]
    // @GetMapping("/credit/checkout")
    // public String checkout(@RequestParam("productNo") int productNo
    //                       ,@RequestParam("orderNo") int orderNo 
    //                       ,Model model) throws Exception {
        
    //     Order order = companyService.selectOrder(orderNo);  // orderNo로 주문 정보 조회
    //     Product product = companyService.selectProduct(productNo);
        
    //     model.addAttribute("order", order);
    //     model.addAttribute("product", product);
    //     return "/company/credit/checkout";
    // }

    // 토스 페이먼츠 메인 [GET] ⭕⭕⭕
    @GetMapping("/credit/checkout")
    public ResponseEntity<?> checkout(@RequestParam("productNo") int productNo, 
                                      @RequestParam("orderNo") int orderNo,
                                      @RequestParam("userNo") int userNo) {
        try {
            Order order = companyService.selectOrder(orderNo);  
            Product product = companyService.selectProduct(productNo);
            Users user = userService.selectByUserNo(userNo); 

            if (product == null || order == null || user == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("product", product);
            response.put("user", user); 

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 토스 페이먼츠 중간 프로세스 [GET]
    // 화면을 보여줄 필요가 없다 ⭕⭕⭕
    // @GetMapping("/credit/process")
    // public String process() throws Exception {
        
    //     return "/company/credit/process";
    // }





    // 주문 테이블 추가 [POST] ⭕⭕⭕
    @ResponseBody
    @PostMapping("/credit/checkout")
    public ResponseEntity<?> successPro(@RequestBody Map<String, Integer> request) throws Exception {
        int userNo = request.get("userNo");
        int productNo = request.get("productNo");

        log.info("제품번호 : " + productNo);

        Users user = userService.selectByUserNo(userNo);

        // 결제진행시 주문테이블에 미결제 등록
        Order order = new Order();
        Product product = companyService.selectProduct(productNo);

        order.setUserNo(user.getUserNo()); 
        order.setProductNo(product.getProductNo());
        order.setTotalQuantity(product.getProductCount()); // 필요한 경우 적절히 설정
        order.setTotalPrice(product.getProductPrice());
        order.setOrderStatus("PENDING");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, product.getProductDuration());
        order.setExpirationDate(calendar.getTime()); // 만료일 개월수만큼 더해서 나오게끔해야됨
        order.setAccessOrder(1);

        // order_no를 반환하는 insertOrder 메서드 호출
        int orderNo = companyService.insertOrder(order);
        log.info("주문번호 : " + orderNo);

        if (orderNo > 0) {            
            return new ResponseEntity<>(Map.of("success", true, "orderNo", orderNo), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("success", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




     // 결제 테이블 추가 [POST] ⭕⭕⭕ 결제 2번 들어가는건 <StrictMode> 때문 
    @PostMapping("/credit/process")
    public ResponseEntity<?> successPro(@RequestBody OrderCreditDTO orderCreditDTO ) throws Exception {

        int orderNo = orderCreditDTO.getOrderNo();
        int productNo = orderCreditDTO.getProductNo();
        String orderId = orderCreditDTO.getOrderId();

        log.info(":::::::::::::::::::::::주문번호 : " + orderNo);
        log.info(":::::::::::::::::::::::상품번호 : " + productNo);


        Order order = companyService.selectOrder(orderNo);
        Product product = companyService.selectProduct(productNo);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, product.getProductDuration());
        order.setExpirationDate(calendar.getTime()); // 만료일 개월수만큼 더해서 나오게끔해야됨
        order.setRemainQuantity(order.getTotalQuantity());

        int result = companyService.updateOrder(order); // 주문 갱신

        if(result>0){
            log.info(" order_status / updated_date / expiration_date 갱신 ");
        }else{
            log.info(" 주문 갱신 실패 ");
        }
    
        Credit credit = new Credit();
        credit.setOrderNo(orderNo);
        credit.setCreditCode(orderId);
        credit.setCreditMethod("간편결제");
        credit.setCreditStatus("PAID");

        int creditResult = companyService.insertCredit(credit); // 결제 등록

        if(creditResult > 0) {
            return new ResponseEntity<Object>(credit, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






    // 토스 페이먼츠 success [GET]
    // @GetMapping("/credit/success")
    // public String success(@RequestParam("productNo") int productNo
    //                     ,@RequestParam("orderNo") int orderNo
    //                     ,Model model) throws Exception {

    //     Product product = companyService.selectProduct(productNo);
    //     Order order = companyService.selectOrder(orderNo);
    //     Credit credit = companyService.selectCredit(orderNo);

    //     model.addAttribute("credit", credit);
    //     model.addAttribute("order", order);
    //     model.addAttribute("product", product);
    //     return "/company/credit/success";
    // }

    // 토스 페이먼츠 success [GET] ⭕⭕⭕
    @GetMapping("/credit/success")
    public ResponseEntity<?> success(@RequestParam("productNo") int productNo
                                    ,@RequestParam("orderNo") int orderNo
                                    ,@RequestParam("userNo") int userNo) {
        try {
            Product product = companyService.selectProduct(productNo);
            Order order = companyService.selectOrder(orderNo);
            Credit credit = companyService.selectCredit(orderNo);
            Users user = userService.selectByUserNo(userNo);

            if (product == null || order == null || credit == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("order", order);
            response.put("credit", credit);
            response.put("user", user);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 토스 페이먼츠 fail [GET] ⭕⭕⭕
    @GetMapping("/credit/fail")
    public String fail() {
        return "/company/credit/fail";
    }


    // 결제 목록 내역 화면 [GET]
    @GetMapping("/credit/credit_list_com")
    public ResponseEntity<Map<String, Object>> creditListCom(@RequestParam("userNo") int userNo,
                                                            Page page) {
        try {
            log.info("userNo" + userNo);
    
            Users user = userService.selectByUserNo(userNo);
            List<Order> orderCreditList = companyService.orderCreditList(userNo, page);
    
            Map<String, Object> response = new HashMap<>();
            response.put("orderCreditList", orderCreditList);
            response.put("user", user);
            log.info("orderCreditList" + orderCreditList);
    
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    



//-------결제------------------------------------------------------------------------

    
    // 기업상세정보페이지 [유저]
    // 채용공고 상세 페이지 ----
    // @GetMapping("/com_detail_user")
    // public String getMethodName(@RequestParam("comNo") Integer comNo, Model model,
    //         HttpSession session) throws Exception {
        
    //     // Users user = (Users) session.getAttribute("user");
        
    //     // log.info("@@@@@@@@@@@@@" + comNo);
    //     // RecruitPost recruitPost = recruitService.recruitRead(recruitNo);
    //     // if (recruitPost == null) {
    //         // log.error("RecruitPost 객체가 null입니다. : ", recruitPost);
    //     // } else {
    //         // log.info("RecruitPost 정보: {}", recruitPost);
    //     // }

    //     // int comNo = recruitPost.getCompany().getComNo();
    //     CompanyDetail companyDetail = recruitService.selectCompanyDetailsWithRecruit(comNo);

    //     // log.info("companyDetail", companyDetail);
    //     model.addAttribute("companyDetail", companyDetail);
    //     // model.addAttribute("recruitPost", recruitPost);

    //     return "/company/com_detail_user";
    // }

    // 기업상세정보페이지 [유저]
    // 채용공고 상세 페이지 ---- ⭕⭕⭕ 근데 그 company 뜨는게 무조건 comNo : 13 /  메타가 뜸
    @GetMapping("/com_detail_user")
    public ResponseEntity<?> getCompanyDetail(@RequestParam("comNo") Integer comNo) {
        try {

            log.info("Received comNo: " + comNo);

            CompanyDetail companyDetail = recruitService.selectCompanyDetailsWithRecruit(comNo);

        return new ResponseEntity<>(companyDetail, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




















    // AI 평가 화면 ///--------------------------------------------------------------------------------------------------------------
    @GetMapping("/score_com")
    public String score_com(Model model, HttpSession session, Page page) throws Exception {
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }
        int comNo = user.getCompany().getComNo();
        // log.info(comNo + "comNO???????@@!@#!@#@!#?!@#?!@?#?!#"); 찍힘 

        List<Resume> applyCvList = recruitService.applyCom(comNo, page);

        for (Resume resume : applyCvList) {
            // log.info("gdgdgddgg" + resume.getCoverLetter());
            // log.info("??????!@#!@#!@#@!" + resume);
        }

        model.addAttribute("resumeList", applyCvList);
        model.addAttribute("page", page);



        return "/company/score_com";
    }
    


}













