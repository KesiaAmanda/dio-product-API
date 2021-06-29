package one.digitalinnovation.productstock.controller;

import one.digitalinnovation.productstock.builder.ProductDTOBuilder;
import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.dto.QuantityDTO;
import one.digitalinnovation.productstock.exception.ProductNotFoundException;
import one.digitalinnovation.productstock.exception.ProductStockExceededException;
import one.digitalinnovation.productstock.exception.ProductStockException;
import one.digitalinnovation.productstock.service.ProductService;
import one.digitalinnovation.productstock.utils.JsonConvertionUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private static final String PRODUCT_API_URL_PATH = "/api/v1/products";
    private static final long VALID_PRODUCT_ID = 1L;
    private static final long INVALID_PRODUCT_ID = 2L;
    private static final String PRODUCT_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String PRODUCT_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAProductIsCreated() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.createProduct(productDTO)).thenReturn(productDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.brand", is(productDTO.getBrand())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Is.is(productDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setBrand(null);

        //then
        mockMvc.perform(post(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.findByName(productDTO.getName())).thenReturn(productDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH + "/" + productDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.brand", is(productDTO.getBrand())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Is.is(productDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredSerialThenNotFoundStatusIsReturned() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.findByName(productDTO.getName())).thenThrow(ProductNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH + "/" + productDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithProductsIsCalledThenOkStatusIsReturned() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.listAll()).thenReturn(Collections.singletonList(productDTO));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(productDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(productDTO.getBrand())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", Is.is(productDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutProductsIsCalledThenOkStatusIsReturned() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productService.listAll()).thenReturn(Collections.singletonList(productDTO));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidSerialThenNoContentIsReturned() throws Exception {
        //given
        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        doNothing().when(productService).deleteBySerial(productDTO.getSerial());

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCT_API_URL_PATH + "/" + productDTO.getSerial())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidSerialThenNotFoundIsReturned() throws Exception {
        //when
        doThrow(ProductNotFoundException.class).when(productService).deleteBySerial(INVALID_PRODUCT_ID);

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCT_API_URL_PATH + "/" + INVALID_PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setQuantity(productDTO.getQuantity() + quantityDTO.getQuantity());

        when(productService.increment(VALID_PRODUCT_ID, quantityDTO.getQuantity())).thenReturn(productDTO);

        mockMvc.perform(patch(PRODUCT_API_URL_PATH + "/" + VALID_PRODUCT_ID + PRODUCT_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.brand", is(productDTO.getBrand())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Is.is(productDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(productDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setQuantity(productDTO.getQuantity() + quantityDTO.getQuantity());

        when(productService.increment(VALID_PRODUCT_ID, quantityDTO.getQuantity())).thenThrow(ProductStockExceededException.class);

        mockMvc.perform(patch(PRODUCT_API_URL_PATH + "/" + VALID_PRODUCT_ID + PRODUCT_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidProductIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(productService.increment(INVALID_PRODUCT_ID, quantityDTO.getQuantity())).thenThrow(ProductNotFoundException.class);
        mockMvc.perform(patch(PRODUCT_API_URL_PATH + "/" + INVALID_PRODUCT_ID + PRODUCT_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setQuantity(productDTO.getQuantity() - quantityDTO.getQuantity());

        when(productService.decrement(VALID_PRODUCT_ID, quantityDTO.getQuantity())).thenReturn(productDTO);

        mockMvc.perform(patch(PRODUCT_API_URL_PATH + "/" + VALID_PRODUCT_ID + PRODUCT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.brand", is(productDTO.getBrand())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Is.is(productDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(productDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        ProductDTO productDTO = ProductDTOBuilder.builder().build().toProductDTO();
        productDTO.setQuantity(productDTO.getQuantity() - quantityDTO.getQuantity());

        when(productService.decrement(VALID_PRODUCT_ID, quantityDTO.getQuantity())).thenThrow(ProductStockException.class);

        mockMvc.perform(patch(PRODUCT_API_URL_PATH + "/" + VALID_PRODUCT_ID + PRODUCT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidProductIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(productService.decrement(INVALID_PRODUCT_ID, quantityDTO.getQuantity())).thenThrow(ProductNotFoundException.class);
        mockMvc.perform(patch(PRODUCT_API_URL_PATH + "/" + INVALID_PRODUCT_ID + PRODUCT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}

