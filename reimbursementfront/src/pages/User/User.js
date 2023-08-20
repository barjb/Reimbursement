import Header from "../../components/Header";
import Footer from '../../components/Footer';
import { Container, Form, FormGroup, Button, Row, Col, Alert } from "react-bootstrap";

import React, { useState, useEffect, useCallback } from "react";
import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";

import { getLimits, postReimbursement } from "../../services/userservice";

export default function User() {

    const today = new Date();
    today.setMilliseconds(0);
    const [showAlert, setShowAlert] = useState(false);
    const [submitAlertText, setSubmitAlertText] = useState('');
    const [variant, setVariant] = useState('success');
    const [ticketTypes, setTicketTypes] = useState([]);
    const [limits, setLimits] = useState();
    const [startDate, setStartDate] = useState(today);
    const [allowanceStartDate, setAllowanceStartDate] = useState(today);
    const [allowanceEndDate, setAllowanceEndDate] = useState(today);
    const [days, setDays] = useState([]);
    const [distance, setDistance] = useState(0);
    const [total, setTotal] = useState(0);
    const [selectedTicket, setSelectedTicket] = useState();
    const [receipts, setReceipts] = useState([]);

    useEffect(() => {
        getLimits().then((res) => {
            console.log(res.data);
            setLimits(res.data);
            const receiptTypes = res.data.receipts.filter(receipt => receipt.isActive).map(receipt => {
                const lower = receipt.receiptType.toLowerCase();
                return lower.charAt(0).toUpperCase() + lower.slice(1);
            });
            receiptTypes.unshift('Add new receipt');
            setTicketTypes(receiptTypes);
        })
    }, []);

    const currentTotal = useCallback(() => {
        let total = 0;
        if (receipts !== undefined && days !== undefined && limits !== undefined && distance !== undefined) {
            receipts.forEach(receipt => {
                total += Number(receipt.expense);
            });
            total += days.filter(day => day.enabled === true).length * limits.dailyAllowance;
            total += Number(distance) * limits.carMileage;
        }
        return total;
    }, [days, distance, limits, receipts]);

    useEffect(() => {
        setTotal(currentTotal());
    }, [receipts, days, distance, currentTotal]);

    const handleSubmit = (e) => {
        e.preventDefault();
        currentTotal();
        const body = {
            'tripDate': startDate,
            'receipt': receipts,
            'allowances': {
                'starDate': allowanceStartDate,
                'endDate': allowanceEndDate,
                'excludeDays': days.filter(e => e.enabled === false).map(obj => obj.day)
            },
            'distance': distance,
        }
        console.log({ body });
        postReimbursement(body).then(res => {
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

    function daysBetween(start, end) {
        const days = [];
        let current = new Date(start);
        while (current.getTime() <= end.getTime()) {
            days.push({ day: new Date(current), enabled: true });
            current.setDate(current.getDate() + 1);
            current.setMilliseconds(0);
        }
        return days;
    }

    useEffect(() => {
        setDays(daysBetween(allowanceStartDate, allowanceEndDate));
    }, [allowanceStartDate, allowanceEndDate])

    const setTicketType = (receiptType) => {
        console.log('setTicketType')
        const updatedReceipts = [...receipts];
        updatedReceipts[receipts.length] = { "receiptType": receiptType, "expense": 0 };
        setReceipts(updatedReceipts);
        setSelectedTicket('Add new receipt');
    }

    const handleReceiptChange = (index, value) => {
        const updatedReceipts = [...receipts];
        updatedReceipts[index] = { "receiptType": updatedReceipts[index].receiptType, "expense": value };
        setReceipts(updatedReceipts);
        setTotal(currentTotal())
    }

    const handleDeleteReceipt = (index) => {
        const updatedReceipts = [...receipts];
        updatedReceipts.splice(index, 1);
        setReceipts(updatedReceipts);
    }

    const handleStartDate = date => {
        if (date === null) return;
        date.setMilliseconds(0);
        setStartDate(date);
        if (date <= allowanceStartDate && date <= allowanceEndDate) return;
        if (date > allowanceStartDate) setAllowanceStartDate(date);
        if (allowanceEndDate < date) setAllowanceEndDate(date);
    }

    const handleAllowanceStartDate = (date) => {
        if (date === null) return;
        if (date < startDate) return;
        date.setMilliseconds(0);
        setAllowanceStartDate(date);
        if (date > allowanceEndDate) setAllowanceEndDate(date);
    }

    const handleAllowanceEndDate = (date) => {
        if (date === null) return;
        if (date < startDate || date < allowanceStartDate) return;
        date.setMilliseconds(0);
        setAllowanceEndDate(date);
    }

    const handleDistanceChange = (distance) => {
        setDistance(distance);
    }

    const updateExcludeDays = (index) => {
        const updatedDays = [...days];
        updatedDays[index].enabled = !updatedDays[index].enabled;
        setDays(updatedDays);
    }

    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    return (
        <>
            <Header></Header>
            <Container>
                <Col md={5}>
                    <Form onSubmit={handleSubmit}>
                        <FormGroup className="mb-3">
                            <Row>
                                <Col>
                                    <Form.Label>Trip Date</Form.Label>
                                </Col>
                                <Col>
                                    <DatePicker selected={startDate} onChange={(date) => handleStartDate(date)} dateFormat="dd/MM/yyyy" className="form-control" />
                                </Col>
                            </Row>

                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Form.Label>Add receipts by ticket type</Form.Label>
                            <Form.Control as='select' onChange={(e) => { setTicketType(e.target.value) }} value={selectedTicket}>
                                {ticketTypes.map((ticket, index) => (
                                    <option value={ticket} key={"ticket" + index} > {ticket}</option>
                                ))}
                            </Form.Control>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            {receipts.map((receipt, index) => (
                                <Row key={'receipt' + index} className="mb-1 align-items-center">
                                    <Col xs={{ offset: 1 }}>
                                        <Form.Label>{receipt.receiptType}</Form.Label>
                                    </Col>
                                    <Col xs='auto'>
                                        <Form.Control type="number" value={receipt.expense} onChange={(e) => handleReceiptChange(index, e.target.value)}></Form.Control>
                                    </Col>
                                    <Col xs='auto' className="justify-content-end">
                                        <Button type="button" variant="danger" onClick={() => handleDeleteReceipt(index)}>-</Button>
                                    </Col>
                                </Row>
                            ))}
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Row>
                                <Col>
                                    <Form.Label>Allowance start day</Form.Label>
                                </Col>
                                <Col>
                                    <DatePicker selected={allowanceStartDate} onChange={(date) => handleAllowanceStartDate(date)} dateFormat="dd/MM/yyyy" className="form-control" />
                                </Col>
                            </Row>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Row>
                                <Col>
                                    <Form.Label>Allowance end date</Form.Label>
                                </Col>
                                <Col>
                                    <DatePicker selected={allowanceEndDate} onChange={(date) => handleAllowanceEndDate(date)} dateFormat="dd/MM/yyyy" className="form-control" />
                                </Col>
                            </Row>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            {days.map((day, index) => (
                                <Row key={'days' + index} className="mb-1 align-items-center">
                                    <Col xs={{ offset: 1 }}>
                                        <Form.Control type="date" value={formatDate(day.day)} disabled={true} ></Form.Control>
                                    </Col>
                                    <Col xs='auto' className="justify-content-end">
                                        <Form.Check type="checkbox" label='exclude' value={day.enabled} onChange={() => updateExcludeDays(index)}></Form.Check>
                                    </Col>
                                </Row>
                            ))}
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Form.Label>Distance</Form.Label>
                            <Form.Control type="number" value={distance} onChange={(e) => handleDistanceChange(e.target.value)}></Form.Control>
                        </FormGroup>

                        <FormGroup className="mb-3">
                            <Form.Label>Total reimbursement</Form.Label>
                            <Form.Control type="number" value={total} disabled={true}></Form.Control>
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