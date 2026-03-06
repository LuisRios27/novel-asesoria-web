document.getElementById("submit-btn").addEventListener("click", function(event) {
    // ¡NUNCA OLVIDAR ESTA LÍNEA EN FORMULARIOS!
    event.preventDefault(); 

    var usernameInput = document.getElementById("username").value;
    var passwordInput = document.getElementById("password").value;

    fetch("/api/usuarios/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: usernameInput,
            password: passwordInput
        })
    })
    .then(function(response) {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Credenciales incorrectas");
        }
    })
    .then(function(usuario) {
        // Guardamos los datos en el navegador para usarlos en las otras pantallas
        localStorage.setItem("usuarioId", usuario.id);
        localStorage.setItem("usuario", JSON.stringify(usuario));

        // El gran viaje: Redirección según el rol
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