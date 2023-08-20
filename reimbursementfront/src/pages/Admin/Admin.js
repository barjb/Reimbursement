import Header from "../../components/Header";
import Footer from '../../components/Footer';
import { Container, Form, FormGroup, Button, Row, Col, Alert } from "react-bootstrap";
import React, { useState, useEffect } from "react";
import { getLimits, postLimits } from "../../services/adminservice";

export default function Admin() {

    const [showAlert, setShowAlert] = useState(false);
    const [submitAlertText, setSubmitAlertText] = useState('');
    const [variant, setVariant] = useState('success');
    const [dailyAllowance, setDailyAllowance] = useState(0);
    const [mileageReimbursement, setMileageReimbursement] = useState(0);
    const [selectedTicket, setSelectedTicket] = useState();
    const [distanceLimit, setDistanceLimit] = useState(0);
    const [totalLimit, setTotalLimit] = useState(0);
    const [ticketLimits, setTicketLimits] = useState([]);

    useEffect(() => {
        getLimits().then((res) => {
            console.log(res.data);
            setMileageReimbursement(res.data.carMileage);
            setDailyAllowance(res.data.dailyAllowance);
            setDistanceLimit(res.data.distance);
            setTotalLimit(res.data.totalReimbursement);
            setTicketLimits(res.data.receipts.map(e => {
                const lower = e.receiptType.toLowerCase();
                return { receiptType: lower.charAt(0).toUpperCase() + lower.slice(1), limit: e.limit, isActive: e.isActive }
            }));
        });
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        const body = {
            'dailyAllowance': dailyAllowance,
            'carMileage': mileageReimbursement,
            'distance': distanceLimit,
            'totalReimbursement': totalLimit,
            'receipts': ticketLimits,
        }
        console.log({ body });

        postLimits(body).then(res => {
            console.log(res);
            if (res.status === 200) {
                setVariant('success');
                setSubmitAlertText("Form submitted successfully!");
            } else {
                setVariant('danger');
            }
            if (res.code === 'ERR_NETWORK') {
                setVariant('danger');
                setSubmitAlertText("Form submition failed!");
            }
            setShowAlert(true);
        });
    }

    const handleDailyAllowance = (value) => {
        setDailyAllowance(value);
    }

    const handleMileageReimbursement = (value) => {
        setMileageReimbursement(value);
    }

    const handleTicketLimits = (index, value) => {
        const updatedTicketLimits = [...ticketLimits];
        updatedTicketLimits[index] = { "receiptType": updatedTicketLimits[index].receiptType, "limit": value, "isActive": updatedTicketLimits[index].isActive };
        setTicketLimits(updatedTicketLimits);
    }

    const handleDistanceLimit = (value) => {
        setDistanceLimit(value);
    }

    const handleTotalLimit = (value) => {
        setTotalLimit(value);
    }

    const handleIsActiveToggle = (index) => {
        console.log('setTicketType')
        const updatedTicketLimits = [...ticketLimits];
        updatedTicketLimits[index] = { "receiptType": updatedTicketLimits[index].receiptType, "limit": updatedTicketLimits[index].limit, "isActive": !updatedTicketLimits[index].isActive };
        setTicketLimits(updatedTicketLimits);
        setSelectedTicket('');
    }

    return (
        <>
            <Header></Header>
            <Container>
                <Col md={5}>
                    <Form onSubmit={handleSubmit}>
                        <FormGroup className="mb-3">
                            <Form.Label>Daily Allowance</Form.Label>
                            <Form.Control type="number" value={dailyAllowance} onChange={(e) => handleDailyAllowance(e.target.value)}></Form.Control>
                        </FormGroup>
                        <FormGroup className="mb-3">
                            <Form.Label>Car mileage reimbursement</Form.Label>
                            <Form.Control type="number" value={mileageReimbursement} onChange={(e) => handleMileageReimbursement(e.target.value)}></Form.Control>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Form.Label>Visible Receipt Types</Form.Label>
                            <Form.Control as='select' onChange={(e) => { handleIsActiveToggle(e.target.value) }} value={selectedTicket}>
                                <option value={''} key={"ticket-empty"} ></option>
                                {ticketLimits.map((ticket, index) => {
                                    if (ticket.isActive) { return (<option value={index} key={"ticket" + index} > {ticket.receiptType}</option>) }
                                    return null;
                                })}
                            </Form.Control>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Form.Label>Hidden Receipt Types</Form.Label>
                            {ticketLimits.map((receipt, index) => {
                                if (!receipt.isActive) {
                                    return (
                                        <Row key={'receipt' + index} className="mb-1 align-items-center">
                                            <Col xs={{ offset: 1 }}>
                                                <Form.Label>{receipt.receiptType}</Form.Label>
                                            </Col>
                                            <Col xs='auto' className="justify-content-end">
                                                <Button type="button" variant="danger" onClick={() => handleIsActiveToggle(index)}>Set visible</Button>
                                            </Col>
                                        </Row>
                                    )
                                }
                                return null;
                            })}
                        </FormGroup>

                        {ticketLimits.map((ticket, index) => (
                            <FormGroup className="mb-3" key={'ticket' + index}>
                                <Form.Label>{ticket.receiptType} Limit</Form.Label>
                                <Form.Control type='number' onChange={(e) => { handleTicketLimits(index, e.target.value) }} value={ticket.limit}></Form.Control >
                            </FormGroup>
                        ))}

                        <FormGroup className="mb-3">
                            <Form.Label>Distance Limit</Form.Label>
                            <Form.Control type="number" value={distanceLimit} onChange={(e) => handleDistanceLimit(e.target.value)}></Form.Control>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Form.Label>Total Limit</Form.Label>
                            <Form.Control type="number" value={totalLimit} onChange={(e) => handleTotalLimit(e.target.value)}></Form.Control>
                        </FormGroup>
                        <FormGroup className="mb-3 d-flex justify-content-end">
                            <Button type="submit" >Submit</Button>
                        </FormGroup>
                        {showAlert && (
                            <Alert variant={variant} onClose={() => setShowAlert(false)} dismissible>
                                {submitAlertText}
                            </Alert>
                        )}
                    </Form>
                </Col>
            </Container >
            <Footer></Footer>
        </>
    );
}