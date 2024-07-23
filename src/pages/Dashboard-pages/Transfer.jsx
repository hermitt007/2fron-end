import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Navigate from "../../components/navigate";
import { sendTransferData } from "../../services/transferService";
import { getClientAccounts } from "../../services/getAccountsService";
import "../../styles/Dashboard-transfer.css";

const Transfer = () => {
  const navigate = useNavigate();
  const [selectedAccount, setSelectedAccount] = useState("");
  const [amount, setAmount] = useState("");
  const [beneficiary, setBeneficiary] = useState("");
  const [accountNumber, setAccountNumber] = useState("");
  const [description, setDescription] = useState("");
  const [notification, setNotification] = useState("");
  const [showSuccessPopup, setSuccess] = useState(false);

  const accountsFromLocalStorage =
    JSON.parse(localStorage.getItem("accounts")) || [];
  const userAccounts = accountsFromLocalStorage.map((account, index) => ({
    id: index + 1, // Asegúrate de tener un campo id en tus datos del localStorage
    name: account.name, // Nombre de la cuenta
    number: account.account_number, // Número de cuenta
  }));

  const handleTransfer = async (e) => {
    e.preventDefault();
    // Lógica para manejar la transferencia
    const transferData = {
      selectedAccount,
      amount,
      beneficiary,
      accountNumber,
      description,
      notification,
    };
    const response = await sendTransferData(transferData);

    if (response.success) {
      console.log("Transferencia exitosa:", response.data);
      if (response.data.code === "TRANSFER_SUCCESSFUL") {
        setSuccess(true);
        const clientID = localStorage.getItem("clientID");
        const accountsResponse = await getClientAccounts(clientID);
        if (accountsResponse.success) {
          console.log("Cuentas del cliente:", accountsResponse.data);
          localStorage.removeItem("accounts");
          localStorage.setItem(
            "accounts",
            JSON.stringify(accountsResponse.data.accounts_list)
          );
          const data = JSON.parse(localStorage.getItem("accounts"));
          console.log(data);
        } else {
          setError("Error al obtener las cuentas del cliente");
        }
      }

      // navigate('/dashboard'); // Ejemplo de navegación a la página de dashboard después de la transferencia
    } else {
      console.error("Error al realizar la transferencia:", response.error);
      // Manejar el error, por ejemplo, mostrar un mensaje al usuario
    }
  };

  const handleCloseSuccessPopup = () => {
    navigate("/dashboard");
  };

  return (
    <div className="transfer">
      <aside className="sidebar">
        <Navigate />
      </aside>
      <main className="main-content">
        <h1>Transferencias Directas</h1>
        <div className="account-info">
          <label htmlFor="account-select"></label>
          <select
            id="account-select"
            value={selectedAccount}
            onChange={(e) => setSelectedAccount(e.target.value)}
          >
            <option value="">Seleccione una cuenta</option>
            {userAccounts.map((account) => (
              <option key={account.id} value={account.number}>
                {account.name} - {account.number}
              </option>
            ))}
          </select>
        </div>
        <form className="transfer-form" onSubmit={handleTransfer}>
          <div className="form-group">
            <label htmlFor="amount"></label>
            <div className="amount-input">
              <span>$</span>
              <input
                type="number"
                id="amount"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="Ingrese el valor a transferir"
                required
              />
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="beneficiary"></label>
            <input
              type="text"
              id="beneficiary"
              value={beneficiary}
              onChange={(e) => setBeneficiary(e.target.value)}
              required
              placeholder="Nombre del beneficiario"
            />
            <input
              type="text"
              placeholder="Número de cuenta"
              value={accountNumber}
              onChange={(e) => setAccountNumber(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="description"></label>
            <input
              type="text"
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              maxLength="150"
              placeholder="Descripción (Opcional)"
            />
          </div>
          <div className="form-group">
            <label htmlFor="notification"></label>
            <input
              type="email"
              id="notification"
              value={notification}
              onChange={(e) => setNotification(e.target.value)}
              placeholder="Correo electrónico (Opcional)"
            />
          </div>
          <div className="form-actions">
            <button type="reset" className="btn-secondary">
              Limpiar
            </button>
            <button type="submit" className="btn-primary">
              Continuar
            </button>
          </div>
        </form>
      </main>
      {showSuccessPopup && (
        <div className="success-popup">
          <h1>Transferencia realizada con exito</h1>
          <button onClick={handleCloseSuccessPopup}>Ir a mis cuentas</button>
        </div>
      )}
    </div>
  );
};

export default Transfer;
