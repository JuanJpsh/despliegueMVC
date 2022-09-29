package com.example.demo.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.domain.entity.Producto;
import com.example.demo.domain.entity.Usuario;
import com.example.demo.domain.service.IProductoService;
import com.example.demo.domain.service.IUsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IProductoService productoService;

    Long idLogueado;

    @GetMapping({ "/", "HOME", "index" })
    public String home(Model model) {
        model.addAttribute("allProducts", productoService.getAllProducts());
        return "home";
    }

    @GetMapping("/irSesion")
    public String oauth(Model model) {
        model.addAttribute("usuarioLogueado", new Usuario());
        return "inicio_sesion";
    }

    @PostMapping("/iniciarSesion")
    public String iniciarSesion(@ModelAttribute("usuarioLogueado") Usuario user, Model model) {
        Usuario usuario = usuarioService.findByUserPassword(user.getUsuario(), user.getClave());
        if (usuario.getId() != 0) {
            idLogueado = usuario.getId();
            model.addAttribute("idUsuario", idLogueado);
            model.addAttribute("usuario", usuario.getUsuario());
            return "redirect:/dashboard";
        } else {
            return "credenciales_incorrectas";
        }
    }

    @GetMapping("/actualizar/{id}")
    public String irActualizar(@PathVariable Long id, Model model) {
        Producto producto = productoService.getById(id);
        if (producto.getId() != 0) {
            model.addAttribute("producto", producto);
            return "actualizar";
        } else {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("producto") Producto producto, Model model) {
        Producto productoExistente = productoService.getById(id);

        productoExistente.setNombre(producto.getNombre());
        productoExistente.setPrecio(producto.getPrecio());

        productoService.save(producto);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (idLogueado != 0) {
            model.addAttribute("misProductos", productoService.getPorductsByUser(idLogueado));
            return "mis_productos";
        } else {
            return "HOME";
        }
    }
}
