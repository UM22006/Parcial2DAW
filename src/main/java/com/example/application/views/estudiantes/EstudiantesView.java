package com.example.application.views.estudiantes;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.application.data.Estudiante;
import com.example.application.services.EstudianteService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Estudiantes")
@Route(value = "estudiantes", layout = MainLayout.class)
@PreAuthorize("hasRole('ADMIN')")
public class EstudiantesView extends VerticalLayout implements BeforeEnterObserver {

    private final EstudianteService estudianteService;

    private final TextField carnet = new TextField("Carnet");
    private final TextField primerNombre = new TextField("Primer Nombre");
    private final TextField segundoNombre = new TextField("Segundo Nombre");
    private final TextField primerApellido = new TextField("Primer Apellido");
    private final TextField segundoApellido = new TextField("Segundo Apellido");
    private final TextField numeroTelefono = new TextField("Teléfono");
    private final EmailField correo = new EmailField("Correo");
    private final IntegerField edad = new IntegerField("Edad");
    private final TextField direccion = new TextField("Dirección");
    private final TextField carrera = new TextField("Carrera");

    private final Button guardar = new Button("💾 Guardar");
    private final Button limpiar = new Button("🩹 Limpiar");

    private final Grid<Estudiante> grid = new Grid<>(Estudiante.class, false);
    private final BeanValidationBinder<Estudiante> binder = new BeanValidationBinder<>(Estudiante.class);

    private Estudiante estudianteActual;

    public EstudiantesView(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;

        // ✅ Imprimir los roles del usuario actual
        System.out.println("🔐 Roles del usuario actual:");
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            System.out.println(" - " + authority.getAuthority());
        }

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("🎓 Registro de Estudiantes");

        FormLayout formLayout = crearFormulario();
        configurarBotones();
        configurarGrid();
        actualizarGrid();

        HorizontalLayout acciones = new HorizontalLayout(guardar, limpiar);
        acciones.setSpacing(true);
        acciones.setJustifyContentMode(JustifyContentMode.CENTER);
        acciones.setWidthFull();
        acciones.getStyle().set("margin-top", "0.5em");

        add(titulo, formLayout, acciones, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            event.rerouteTo("inicio");
        }
    }

    private FormLayout crearFormulario() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        formLayout.add(
            carnet, primerNombre, segundoNombre,
            primerApellido, segundoApellido,
            numeroTelefono, correo, edad,
            direccion, carrera
        );

        formLayout.setColspan(direccion, 2);
        formLayout.setColspan(carrera, 2);

        binder.bindInstanceFields(this);
        return formLayout;
    }

    private void configurarBotones() {
        guardar.setWidth("160px");
        guardar.getStyle()
            .set("background-color", "#2563eb")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-size", "14px");

        limpiar.setWidth("160px");
        limpiar.getStyle()
            .set("background-color", "#6b7280")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-size", "14px");

        guardar.addClickListener(e -> guardarEstudiante());
        limpiar.addClickListener(e -> limpiarFormulario());
    }

    private void guardarEstudiante() {
        Estudiante nuevo = (estudianteActual != null) ? estudianteActual : new Estudiante();

        if (binder.writeBeanIfValid(nuevo)) {
            estudianteService.save(nuevo);
            Notification.show("✅ Estudiante guardado con éxito", 3000, Notification.Position.MIDDLE);
            estudianteActual = null;
            binder.readBean(null);
            actualizarGrid();
        } else {
            Notification.show("❌ Corrige los errores del formulario", 4000, Notification.Position.MIDDLE);
        }
    }

    private void limpiarFormulario() {
        estudianteActual = null;
        binder.readBean(null);
    }

    private void configurarGrid() {
        grid.removeAllColumns();

        grid.addColumn(Estudiante::getCarnet).setHeader("Carnet");
        grid.addColumn(Estudiante::getPrimerNombre).setHeader("Primer Nombre");
        grid.addColumn(Estudiante::getSegundoNombre).setHeader("Segundo Nombre");
        grid.addColumn(Estudiante::getPrimerApellido).setHeader("Primer Apellido");
        grid.addColumn(Estudiante::getSegundoApellido).setHeader("Segundo Apellido");
        grid.addColumn(Estudiante::getNumeroTelefono).setHeader("Teléfono");
        grid.addColumn(Estudiante::getCorreo).setHeader("Correo");
        grid.addColumn(Estudiante::getEdad).setHeader("Edad");
        grid.addColumn(Estudiante::getDireccion).setHeader("Dirección");
        grid.addColumn(Estudiante::getCarrera).setHeader("Carrera");

        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        grid.getStyle().set("overflow-x", "auto");
    }

    private void editarEstudiante(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        binder.readBean(estudiante);
    }

    private void actualizarGrid() {
        grid.setItems(estudianteService.findAll());
    }
}
