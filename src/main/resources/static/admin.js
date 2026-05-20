// authOptions está en el nivel global para que TODAS las funciones
// del archivo puedan usarla, tanto las de adentro del DOMContentLoaded
// como las de afuera (avanzarTramite, reiniciarTramites, eliminarUsuario).
function authOptions(method, body) {
    var token = localStorage.getItem("token");
    var options = {
        method: method || "GET",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        }
    };
    if (body) options.body = JSON.stringify(body);
    return options;
}

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

    fetch("/api/usuarios", authOptions())
    .then(response => response.json())
    .then(usuarios => {
        var tbody = document.querySelector("#student-table tbody");
        tbody.innerHTML = "";

        usuarios.forEach(user => {
            if (user.rol === 'ESTUDIANTE') {
                var row = document.createElement("tr");
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

    var modal = document.getElementById("create-modal");
    var createBtn = document.getElementById("create-user-btn");
    var closeBtn = document.getElementsByClassName("close-btn")[0];

    createBtn.addEventListener("click", function() {
        modal.style.display = "block";
    });

    closeBtn.addEventListener("click", function() {
        modal.style.display = "none";
    });

    window.addEventListener("click", function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    });

    document.getElementById("create-user-form").addEventListener("submit", function(event) {
        event.preventDefault();

        var nuevoUsuario = {
            nombres: document.getElementById("new-nombres").value,
            apellidos: document.getElementById("new-apellidos").value,
            universidad: document.getElementById("new-universidad").value,
            carrera: document.getElementById("new-carrera").value,
            username: document.getElementById("new-username").value,
            password: document.getElementById("new-password").value
        };

        fetch("/api/usuarios", authOptions("POST", nuevoUsuario))
        .then(function(response) {
            if (!response.ok) {
                return response.text().then(function(textoError) {
                    throw new Error(textoError);
                });
            }
            return response.json();
        })
        .then(function() {
            alert("¡Estudiante creado con éxito! Ya puede iniciar sesión.");
            modal.style.display = "none";
            location.reload();
        })
        .catch(function(error) {
            alert(error.message);
        });
    });
});

// --- FUNCIONES DE LOS BOTONES ---
// Estas funciones están fuera del DOMContentLoaded porque son llamadas
// desde el HTML inline (onclick="..."). Pueden usar authOptions porque
// ahora está en el scope global.

function avanzarTramite(estudianteId) {
    fetch("/api/usuarios/" + estudianteId + "/tramites", authOptions())
    .then(res => res.json())
    .then(tramites => {
        var tramiteActual = tramites.find(t => t.estado === "EN_PROCESO");
        if (!tramiteActual) {
            alert("Este estudiante no tiene trámites en proceso.");
            return;
        }
        fetch("/api/tramites/" + tramiteActual.id + "/avanzar", authOptions("PUT"))
        .then(() => location.reload());
    });
}

function reiniciarTramites(usuarioId) {
    if (confirm("¿Estás seguro de que quieres DEVOLVER AL PASO 1 a este estudiante?")) {
        fetch("/api/tramites/usuario/" + usuarioId + "/reiniciar", authOptions("PUT"))
        .then(() => location.reload());
    }
}

function eliminarUsuario(usuarioId) {
    if (confirm("🚨 ¡CUIDADO! ¿Estás seguro de que quieres ELIMINAR a este estudiante y todo su historial? Esta acción no se puede deshacer.")) {
        fetch("/api/usuarios/" + usuarioId, authOptions("DELETE"))
        .then(() => location.reload());
    }
}