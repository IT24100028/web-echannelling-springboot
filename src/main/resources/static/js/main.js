let currentUser = null;
let selectedDoctor = null;
let selectedTimeSlot = null;


document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {

    checkAuthStatus();
    

    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    

    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}


function checkAuthStatus() {
    const user = localStorage.getItem('currentUser');
    if (user) {
        currentUser = JSON.parse(user);
        updateNavbar();
    }
}

function signIn(email, password, userType = 'patient') {

    return new Promise((resolve, reject) => {
        setTimeout(() => {
            // Mock authentication
            if (email && password) {
                const user = {
                    id: Date.now(),
                    email: email,
                    name: email.split('@')[0],
                    type: userType,
                    token: 'mock-token-' + Date.now()
                };
                
                localStorage.setItem('currentUser', JSON.stringify(user));
                currentUser = user;
                updateNavbar();
                resolve(user);
            } else {
                reject(new Error('Invalid credentials'));
            }
        }, 1000);
    });
}

function signUp(userData) {

    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (userData.email && userData.password) {
                const user = {
                    id: Date.now(),
                    ...userData,
                    token: 'mock-token-' + Date.now()
                };
                
                localStorage.setItem('currentUser', JSON.stringify(user));
                currentUser = user;
                updateNavbar();
                resolve(user);
            } else {
                reject(new Error('Invalid user data'));
            }
        }, 1000);
    });
}

function signOut() {
    localStorage.removeItem('currentUser');
    currentUser = null;
    updateNavbar();
    window.location.href = 'index.html';
}

function updateNavbar() {
    const authLinks = document.querySelectorAll('.auth-link');
    const userLinks = document.querySelectorAll('.user-link');
    
    if (currentUser) {
        authLinks.forEach(link => link.style.display = 'none');
        userLinks.forEach(link => link.style.display = 'block');
        
        // Update user name if element exists
        const userNameElement = document.getElementById('userName');
        if (userNameElement) {
            userNameElement.textContent = currentUser.name;
        }
    } else {
        authLinks.forEach(link => link.style.display = 'block');
        userLinks.forEach(link => link.style.display = 'none');
    }
}

// Form handling functions
function handleSignIn(event) {
    event.preventDefault();
    
    const form = event.target;
    const email = form.querySelector('[name="email"]').value;
    const password = form.querySelector('[name="password"]').value;
    const userType = form.querySelector('[name="userType"]')?.value || 'patient';
    
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    
    submitBtn.innerHTML = '<span class="loading"></span> Signing in...';
    submitBtn.disabled = true;
    
    signIn(email, password, userType)
        .then(user => {
            showAlert('Successfully signed in!', 'success');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        })
        .catch(error => {
            showAlert(error.message, 'danger');
        })
        .finally(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
}

function handleSignUp(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    const userData = Object.fromEntries(formData.entries());
    
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    
    submitBtn.innerHTML = '<span class="loading"></span> Creating account...';
    submitBtn.disabled = true;
    
    signUp(userData)
        .then(user => {
            showAlert('Account created successfully!', 'success');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        })
        .catch(error => {
            showAlert(error.message, 'danger');
        })
        .finally(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
}

// Search and filter functions
function searchDoctors(query, specialty = '') {
    // Mock doctor data
    const doctors = [
        {
            id: 1,
            name: 'Dr. Sarah Johnson',
            specialty: 'Cardiology',
            experience: '15 years',
            rating: 4.8,
            image: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80',
            available: true
        },
        {
            id: 2,
            name: 'Dr. Michael Chen',
            specialty: 'Neurology',
            experience: '12 years',
            rating: 4.6,
            image: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80',
            available: true
        },
        {
            id: 3,
            name: 'Dr. Emily Rodriguez',
            specialty: 'Pediatrics',
            experience: '8 years',
            rating: 4.9,
            image: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80',
            available: true
        },
        {
            id: 4,
            name: 'Dr. David Kim',
            specialty: 'Orthopedics',
            experience: '20 years',
            rating: 4.7,
            image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80',
            available: false
        }
    ];
    
    return doctors.filter(doctor => {
        const matchesQuery = doctor.name.toLowerCase().includes(query.toLowerCase()) ||
                           doctor.specialty.toLowerCase().includes(query.toLowerCase());
        const matchesSpecialty = !specialty || doctor.specialty === specialty;
        return matchesQuery && matchesSpecialty;
    });
}

function displayDoctors(doctors) {
    const container = document.getElementById('doctorsContainer');
    if (!container) return;
    
    container.innerHTML = '';
    
    if (doctors.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="fas fa-search fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">No doctors found</h4>
                <p class="text-muted">Try adjusting your search criteria</p>
            </div>
        `;
        return;
    }
    
    doctors.forEach(doctor => {
        const doctorCard = createDoctorCard(doctor);
        container.appendChild(doctorCard);
    });
}

function createDoctorCard(doctor) {
    const col = document.createElement('div');
    col.className = 'col-lg-6 col-xl-4 mb-4';
    
    col.innerHTML = `
        <div class="card doctor-card h-100">
            <div class="card-body p-4">
                <div class="d-flex align-items-center mb-3">
                    <img src="${doctor.image}" alt="${doctor.name}" class="doctor-avatar me-3">
                    <div>
                        <h5 class="card-title mb-1">${doctor.name}</h5>
                        <p class="text-muted mb-0">${doctor.specialty}</p>
                        <div class="d-flex align-items-center mt-1">
                            <i class="fas fa-star text-warning me-1"></i>
                            <span class="text-muted">${doctor.rating}</span>
                            <span class="text-muted ms-2">(${doctor.experience})</span>
                        </div>
                    </div>
                </div>
                <p class="card-text text-muted mb-3">
                    Experienced ${doctor.specialty} specialist with ${doctor.experience} of practice.
                </p>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="badge ${doctor.available ? 'bg-success' : 'bg-danger'}">
                        ${doctor.available ? 'Available' : 'Not Available'}
                    </span>
                    <button class="btn btn-primary btn-sm" 
                            onclick="selectDoctor(${doctor.id})"
                            ${!doctor.available ? 'disabled' : ''}>
                        <i class="fas fa-calendar-plus me-1"></i>Book Appointment
                    </button>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

// Time slot functions
function generateTimeSlots() {
    const slots = [];
    const startHour = 9; // 9 AM
    const endHour = 17; // 5 PM
    
    for (let hour = startHour; hour < endHour; hour++) {
        slots.push(`${hour}:00`);
        slots.push(`${hour}:30`);
    }
    
    return slots;
}

function displayTimeSlots(slots, bookedSlots = []) {
    const container = document.getElementById('timeSlotsContainer');
    if (!container) return;
    
    container.innerHTML = '';
    
    slots.forEach(slot => {
        const isBooked = bookedSlots.includes(slot);
        const slotElement = document.createElement('div');
        slotElement.className = `time-slot ${isBooked ? 'booked' : ''}`;
        slotElement.textContent = slot;
        
        if (!isBooked) {
            slotElement.onclick = () => selectTimeSlot(slot, slotElement);
        }
        
        container.appendChild(slotElement);
    });
}

function selectTimeSlot(time, element) {
    // Remove previous selection
    document.querySelectorAll('.time-slot.selected').forEach(slot => {
        slot.classList.remove('selected');
    });
    
    // Add selection to current element
    element.classList.add('selected');
    selectedTimeSlot = time;
}

function selectDoctor(doctorId) {
    selectedDoctor = doctorId;
    // Navigate to time slots page or show modal
    window.location.href = `time-slots.html?doctor=${doctorId}`;
}

// Booking functions
function bookAppointment() {
    if (!currentUser) {
        showAlert('Please sign in to book an appointment', 'warning');
        return;
    }
    
    if (!selectedDoctor || !selectedTimeSlot) {
        showAlert('Please select a doctor and time slot', 'warning');
        return;
    }
    
    // Simulate booking
    const booking = {
        id: Date.now(),
        userId: currentUser.id,
        doctorId: selectedDoctor,
        timeSlot: selectedTimeSlot,
        date: new Date().toISOString().split('T')[0],
        status: 'confirmed'
    };
    
    // Save to localStorage (in real app, this would be an API call)
    const bookings = JSON.parse(localStorage.getItem('bookings') || '[]');
    bookings.push(booking);
    localStorage.setItem('bookings', JSON.stringify(bookings));
    
    showAlert('Appointment booked successfully!', 'success');
    
    // Reset selections
    selectedDoctor = null;
    selectedTimeSlot = null;
    
    setTimeout(() => {
        window.location.href = 'appointment.html';
    }, 2000);
}

// Admin functions
function loadAdminStats() {
    const bookings = JSON.parse(localStorage.getItem('bookings') || '[]');
    const doctors = JSON.parse(localStorage.getItem('doctors') || '[]');
    
    document.getElementById('totalAppointments').textContent = bookings.length;
    document.getElementById('totalDoctors').textContent = doctors.length;
    document.getElementById('totalBookings').textContent = bookings.length;
    
    // Load recent bookings
    displayRecentBookings(bookings.slice(-5));
}

function displayRecentBookings(bookings) {
    const container = document.getElementById('recentBookings');
    if (!container) return;
    
    container.innerHTML = '';
    
    bookings.forEach(booking => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${booking.id}</td>
            <td>${booking.date}</td>
            <td>${booking.timeSlot}</td>
            <td><span class="badge bg-success">${booking.status}</span></td>
        `;
        container.appendChild(row);
    });
}

// Utility functions
function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        // Create alert container if it doesn't exist
        const container = document.createElement('div');
        container.id = 'alertContainer';
        container.className = 'position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }
    
    const alertId = 'alert-' + Date.now();
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    document.getElementById('alertContainer').innerHTML += alertHtml;
    
    // Auto dismiss after 5 seconds
    setTimeout(() => {
        const alert = document.getElementById(alertId);
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

function formatDate(date) {
    return new Date(date).toLocaleDateString();
}

function formatTime(time) {
    return time;
}

// Export functions for use in other files
window.eChanneling = {
    signIn,
    signUp,
    signOut,
    searchDoctors,
    bookAppointment,
    showAlert,
    currentUser: () => currentUser
};
