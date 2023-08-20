import axios from "axios";

const instance = axios.create();
const hostname = process.env.REACT_APP_BACKEND_HOSTNAME;

export const getLimits = () => {
    return instance.get(`${hostname}/admin`);
}

export const postLimits = async (body) => {
    return instance.post(`${hostname}/admin/`, body).catch((reason) => { return reason; })
}