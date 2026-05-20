document.getElementById("submit-btn").addEventListener("click", function(event) {
    event.preventDefault();

    var usernameInput = document.getElementById("username").value;
    var passwordInput = document.getElementById("password").value;

    fetch("/api/usuarios/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: usernameInput, password: passwordInput })
    })
    .then(function(response) {
        if (response.ok) return response.json();
        throw new Error("Credenciales incorrectas");
    })
    .then(function(respuesta) {
        // El backend ahora devuelve { usuario: {...}, token: "..." }
        var usuario = respuesta.usuario;
        var token = respuesta.token;

        // Guardamos el token y los datos del usuario
        localStorage.setItem("token", token);
        localStorage.setItem("usuario", JSON.stringify(usuario));

        if (usuario.rol === 'ADMIN') {
            window.location.href = "admin.html";
        } else {
            window.location.href = "estudiante.html";
        }
    })
    .catch(function(error) {
        alert("Error: " + error.message);
    });
});