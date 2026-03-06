document.addEventListener("DOMContentLoaded", function() {
    var usuarioString = localStorage.getItem("usuario");

    if (!usuarioString) {
        window.location.href = "index.html";
        return;
    }

    var usuario = JSON.parse(usuarioString);

    // 1. Mostrar los datos del estudiante en la pantalla
    document.getElementById("display-nombres").textContent = usuario.nombres;
    document.getElementById("display-apellidos").textContent = usuario.apellidos;
    document.getElementById("display-universidad").textContent = usuario.universidad;
    document.getElementById("display-carrera").textContent = usuario.carrera;

    // 2. Pedimos los trámites al Backend
    fetch("/api/usuarios/" + usuarioId + "/tramites")
    .then(function(response) {
        if (!response.ok) throw new Error("Error al obtener los trámites");
        return response.json();
    })
    .then(function(tramites) {
        var lineaTiempo = document.getElementById("linea-tiempo");
        lineaTiempo.innerHTML = ""; 

        tramites.forEach(function(tramite) {
            var pasoContainer = document.createElement("div");
            pasoContainer.className = "paso-container";

            var claseEstado = tramite.estado.toLowerCase().replace("_", "-");

            // Escribimos todo el HTML del paso de una sola vez
            pasoContainer.innerHTML = `
                <div class="text"><strong>${tramite.orden}. ${tramite.nombre}</strong></div>
                <div class="circle ${claseEstado}"></div>
                <div class="descripcion-paso">${tramite.descripcion}</div>
            `;

            lineaTiempo.appendChild(pasoContainer);
        });
    })
    .catch(function(error) {
        console.error(error);
    });

    // --- LÓGICA DE LAS FLECHAS DEL CARRUSEL ---
    var viewport = document.getElementById("timeline-viewport");
    
    document.getElementById("btn-prev").addEventListener("click", function() {
        // Desplaza 300px hacia la izquierda suavemente
        viewport.scrollBy({ left: -300, behavior: 'smooth' });
    });

    document.getElementById("btn-next").addEventListener("click", function() {
        // Desplaza 300px hacia la derecha suavemente
        viewport.scrollBy({ left: 300, behavior: 'smooth' });
    });

    // Botón de cerrar sesión
    document.getElementById("logout-btn").addEventListener("click", function() {
        localStorage.clear();
        window.location.href = "index.html";
    });
});