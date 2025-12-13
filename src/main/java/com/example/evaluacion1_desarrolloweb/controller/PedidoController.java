package com.example.evaluacion1_desarrolloweb.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.evaluacion1_desarrolloweb.service.impl.PedidoServiceImpl;
import com.example.evaluacion1_desarrolloweb.entity.Pedido;
import java.util.List;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.example.evaluacion1_desarrolloweb.service.impl.UsuarioServiceImpl;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    // INYECCION DE DEPENDENCIAS Y MÉTODOS PARA GESTIONAR PEDIDOS
    @Autowired
    private PedidoServiceImpl pedidoService;
    @Autowired
    private UsuarioServiceImpl usuarioService;
    @Autowired
    private com.example.evaluacion1_desarrolloweb.service.impl.JuegosServiceImpl juegosService;

    private boolean isAdmin(Usuario usuario) {
        if (usuario == null) return false;
        // Revisa colección de roles con prefijo ROLE_
        boolean byCollection = usuario.getRoles() != null && usuario.getRoles().stream()
            .anyMatch(r -> {
                String n = r.getNombre();
                return n != null && n.toUpperCase().contains("ADMIN");
            });
        if (byCollection) return true;

        // Fallback al legacy getRol() (puede devolver ROLE_ADMIN o ADMIN)
        String rol = usuario.getRol() != null ? usuario.getRol().toUpperCase() : "";
        if (rol.startsWith("ROLE_")) {
            rol = rol.substring(5);
        }
        return rol.contains("ADMIN");
    }
    
    @GetMapping("/listar")
    public String listar(Model model, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra){
        // Obtener el usuario autenticado usando Spring Security
        String email = principal.getName();
        com.example.evaluacion1_desarrolloweb.entity.Usuario usuario = usuarioService.findByEmail(email);
        
        if (usuario == null) {
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/login";
        }

        if (!isAdmin(usuario)) {
            ra.addFlashAttribute("error", "No tienes permisos para ver los pedidos globales");
            return "redirect:/index";
        }

        List<Pedido> pedidos = pedidoService.findAll();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("user", usuario);
        return "pedido/list";
    }

    @GetMapping("/mis")
    public String listarMisPedidos(Model model, java.security.Principal principal) {
        // Obtener el usuario autenticado usando Spring Security
        String email = principal.getName();
        com.example.evaluacion1_desarrolloweb.entity.Usuario usuario = usuarioService.findByEmail(email);
        
        if (usuario == null) {
            return "redirect:/login";
        }

        Long userId = usuario.getId();
        List<Pedido> pedidos = pedidoService.findByUsuarioId(userId);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("user", usuario);
        return "pedido/list";
    }

     @GetMapping("/crear")
    public String crear(@RequestParam(required = false) Long juegoId, Model model, java.security.Principal principal) {
        // Obtener el usuario autenticado usando Spring Security
        String email = principal.getName();
        com.example.evaluacion1_desarrolloweb.entity.Usuario usuario = usuarioService.findByEmail(email);
        
        Pedido pedido = new Pedido();
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("user", usuario);
        // exponer lista de juegos para que el formulario pueda mostrar un selector
        model.addAttribute("juegos", juegosService.findAll());

        if (juegoId != null) {
            com.example.evaluacion1_desarrolloweb.entity.Juegos juego = juegosService.findOne(juegoId);
            if (juego != null) {
                model.addAttribute("juego", juego);
                // prefija el producto con el título del juego
                pedido.setProducto(juego.getTitulo());
            }
        }

        // Asignar usuario al pedido usando Spring Security
        if (usuario != null) {
            pedido.setUsuarioId(usuario);
        }

        return "pedido/create";
    }   

    @PostMapping("/save")
    public String save(@ModelAttribute Pedido pedido, @RequestParam(required = false) Long juegoId, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        // validar cantidad mínima
        Integer cantidadValidar = pedido.getCantidad() != null ? pedido.getCantidad() : 0;
        if (cantidadValidar <= 0) {
            ra.addFlashAttribute("error", "La cantidad debe ser un número entero mayor que 0.");
            return (juegoId != null) ? "redirect:/pedidos/crear?juegoId=" + juegoId : "redirect:/pedidos/crear";
        }

        // Obtener el usuario autenticado usando Spring Security
        String email = principal.getName();
        com.example.evaluacion1_desarrolloweb.entity.Usuario usuario = usuarioService.findByEmail(email);
        
        // Si el formulario incluyó un usuario seleccionado (usuarioId.id), resolver la entidad completa desde la BD.
        if (pedido.getUsuarioId() != null && pedido.getUsuarioId().getId() != null) {
            com.example.evaluacion1_desarrolloweb.entity.Usuario sel = usuarioService.findOne(pedido.getUsuarioId().getId());
            if (sel != null) {
                pedido.setUsuarioId(sel);
            }
        } else {
            // si no tiene usuario, asignarlo desde Spring Security
            if (usuario != null) {
                pedido.setUsuarioId(usuario);
            }
        }

        // si se entregó juegoId, asegurar nombre del producto y decrementar stock
        if (juegoId != null) {
            com.example.evaluacion1_desarrolloweb.entity.Juegos juego = juegosService.findOne(juegoId);
            if (juego != null) {
                pedido.setProducto(juego.getTitulo());
                Integer cantidad = pedido.getCantidad() != null ? pedido.getCantidad() : 1;
                if (cantidad <= 0) {
                    ra.addFlashAttribute("error", "La cantidad debe ser un número entero mayor que 0.");
                    return "redirect:/pedidos/crear?juegoId=" + juegoId;
                }
                Integer stock = juego.getStock() != null ? juego.getStock() : 0;
                if (stock >= cantidad) {
                    juego.setStock(stock - cantidad);
                    juegosService.save(juego);
                } else {
                    ra.addFlashAttribute("error", "No hay stock suficiente para completar el pedido");
                    return "redirect:/pedidos/crear?juegoId=" + juegoId;
                }
            }
        }

        pedidoService.save(pedido);
        ra.addFlashAttribute("success", "Pedido creado correctamente");

        // si quien crea es admin, redirigir al listado global; si no, a "mis" pedidos
        if (usuario != null) {
            if (isAdmin(usuario)) {
                return "redirect:/pedidos/listar";
            }
        }
        return "redirect:/pedidos/mis";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return "redirect:/pedidos/listar";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        Pedido pedido = pedidoService.findOne(id);
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuarios", usuarioService.findAll());
        return "pedido/create";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        // Spring Security ya maneja la autenticación, pero validamos que el usuario tenga rol ADMIN
        String email = principal.getName();
        com.example.evaluacion1_desarrolloweb.entity.Usuario usuario = usuarioService.findByEmail(email);
        
        if (usuario == null) {
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/login";
        }
        
        if (!isAdmin(usuario)) {
            ra.addFlashAttribute("error", "No tienes permisos para eliminar pedidos");
            return "redirect:/index";
        }
        
        try {
            pedidoService.delete(id);
            ra.addFlashAttribute("success", "Pedido eliminado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al eliminar el pedido");
        }
        return "redirect:/pedidos/listar";
    }

    // GENERA UN METODO PARA MOSTRAR EN JSON LOS PEDIDOS
    @GetMapping("/json")
    @ResponseBody
    public List<Pedido> listarJson(){
        return pedidoService.findAll();
    }
}