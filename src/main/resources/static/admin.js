document.addEventListener("DOMContentLoaded", function() {
    var usuarioString = localStorage.getItem("usuario");
    if (!usuarioString) {
        window.location.href = "index.html";
        return;
    }

    var usuario = JSON.parse(usuarioString);
    if (usuario.rol !== 'ADMIN') {
        window.location.href = "estudiante.html";
        return;
    }

    // Cargar la tabla
    fetch("/api/usuarios")
    .then(response => response.json())
    .then(usuarios => {
        var tbody = document.querySelector("#student-table tbody");
        tbody.innerHTML = ""; 

        usuarios.forEach(user => {
            if (user.rol === 'ESTUDIANTE') {
                var row = document.createElement("tr");
                // AQUÍ ESTÁN TUS 3 BOTONES NUEVOS
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.nombres}</td>
                    <td>${user.apellidos}</td>
                    <td>${user.universidad}</td>
                    <td>${user.carrera}</td>
                    <td style="display: flex; gap: 5px;">
                        <button onclick="avanzarTramite(${user.id})" style="background: #28a745; color: white; border: none; padding: 5px; cursor: pointer; border-radius: 3px;">Avanzar</button>
                        <button onclick="reiniciarTramites(${user.id})" style="background: #fd7e14; color: white; border: none; padding: 5px; cursor: pointer; border-radius: 3px;">Reiniciar</button>
                        <button onclick="eliminarUsuario(${user.id})" style="background: #dc3545; color: white; border: none; padding: 5px; cursor: pointer; border-radius: 3px;">Eliminar</button>
                    </td>
                `;
                tbody.appendChild(row);
            }
        });
    });

    document.getElementById("logout-btn").addEventListener("click", function() {
        localStorage.clear();
        window.location.href = "index.html";
    });
});

// --- FUNCIONES DE LOS BOTONES ---

function avanzarTramite(estudianteId) {
    fetch("/api/usuarios/" + estudianteId + "/tramites")
    .then(res => res.json())
    .then(tramites => {
        var tramiteActual = tramites.find(t => t.estado === "EN_PROCESO");
        if (!tramiteActual) {
            alert("Este estudiante no tiene trámites en proceso.");
            return;
        }
        fetch("/api/tramites/" + tramiteActual.id + "/avanzar", { method: "PUT" })
        .then(() => location.reload()); // Recargamos la página al instante
    });
}

function reiniciarTramites(usuarioId) {
    if (confirm("¿Estás seguro de que quieres DEVOLVER AL PASO 1 a este estudiante?")) {
        fetch("/api/tramites/usuario/" + usuarioId + "/reiniciar", { method: "PUT" })
        .then(() => location.reload());
    }
}

function eliminarUsuario(usuarioId) {
    if (confirm("🚨 ¡CUIDADO! ¿Estás seguro de que quieres ELIMINAR a este estudiante y todo su historial? Esta acción no se puede deshacer.")) {
        fetch("/api/usuarios/" + usuarioId, { method: "DELETE" })
        .then(() => location.reload());
    }
}

// --- MAGIA DEL MODAL PARA CREAR USUARIO ---

var modal = document.getElementById("create-modal");
var createBtn = document.getElementById("create-user-btn");
var closeBtn = document.getElementsByClassName("close-btn")[0];

// 1. Abrir la ventana
createBtn.addEventListener("click", function() {
    modal.style.display = "block";
});

// 2. Cerrar con la "X"
closeBtn.addEventListener("click", function() {
    modal.style.display = "none";
});

// 3. Cerrar si haces clic afuera de la ventana blanca
window.addEventListener("click", function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
});

// 4. Enviar los datos al Backend
document.getElementById("create-user-form").addEventListener("submit", function(event) {
    event.preventDefault(); // ¡Evitamos que la página se recargue!

    // Capturamos lo que escribió el Admin
    var nuevoUsuario = {
        nombres: document.getElementById("new-nombres").value,
        apellidos: document.getElementById("new-apellidos").value,
        universidad: document.getElementById("new-universidad").value,
        carrera: document.getElementById("new-carrera").value,
        username: document.getElementById("new-username").value,
        password: document.getElementById("new-password").value
    };

    // Se lo mandamos a nuestro endpoint POST /api/usuarios
    fetch("/api/usuarios", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(nuevoUsuario)
    })
    
    .then(function(response) {
        if (!response.ok) {
            // Si hay un error (ej. 400 Bad Request), leemos el texto que manda Java
            return response.text().then(function(textoError) {
                throw new Error(textoError); 
            });
        }
        return response.json(); // Si todo está bien, seguimos
    })
    .then(function(usuarioGuardado) {
        alert("¡Estudiante creado con éxito! Ya puede iniciar sesión.");
        modal.style.display = "none"; 
        location.reload(); 
    })
    .catch(function(error) {
        // Aquí mostraremos la alerta con el texto: "Error: El nombre de usuario ya está en uso."
        alert(error.message); 
    });

});