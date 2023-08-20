import axios from "axios";

const instance = axios.create();
const hostname = process.env.REACT_APP_BACKEND_HOSTNAME;

export const getLimits = () => {
    return instance.get(`${hostname}/user`);
}

export const postReimbursement = (body) => {
    return instance.post(`${hostname}/user/`, body).catch((reason) => { return reason; })

}