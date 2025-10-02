# 📧 **TODOs de Notificaciones - Sistema de Reservas de Cabañas**

## 🎯 **Resumen**

Este documento detalla todas las funcionalidades de notificaciones pendientes de implementación en el sistema de reservas de cabañas.

---

## 📋 **Funcionalidades Pendientes**

### **1. 🔐 Password Reset Email**

**Ubicación:** `src/main/java/com/cooperative/cabin/application/service/AuthApplicationService.java:104`

**Estado:** ⏳ **PENDIENTE**

**Funcionalidad actual:**

- ✅ Generación de tokens de recuperación
- ✅ Validación de tokens
- ✅ Almacenamiento en base de datos
- ❌ **Envío de emails**

**Requerimientos para implementación:**

1. **Crear NotificationService interface y implementación**
2. **Integrar con proveedor de email** (SendGrid, AWS SES, etc.)
3. **Crear template de email** para password reset
4. **Configurar variables de entorno** para credenciales de email
5. **Manejar errores de envío** de email
6. **Agregar logs** de envío exitoso/fallido
7. **Considerar cola de emails** para envío asíncrono

**Implementación sugerida:**

```java
notificationService.sendPasswordResetEmail(email, token, documentNumber);
```

---

### **2. 📧 Waiting List Notifications**

**Ubicación:** `src/main/java/com/cooperative/cabin/application/service/WaitingListApplicationServiceImpl.java:47`

**Estado:** ⏳ **PENDIENTE**

**Funcionalidad actual:**

- ✅ Generación de tokens de notificación
- ✅ Ventana de expiración configurable
- ✅ Almacenamiento en base de datos
- ✅ Scheduler de expiración automática
- ❌ **Envío de emails/SMS**

**Requerimientos para implementación:**

1. **Enviar email al usuario** notificado con el token
2. **Incluir información** de la cabaña y fechas disponibles
3. **Incluir link** para reclamar la reserva
4. **Configurar template de email** para waiting list notification
5. **Manejar errores de envío**
6. **Considerar notificación por SMS** como alternativa

**Implementación sugerida:**

```java
notificationService.sendWaitingListNotification(
    next.getUser().getEmail(),
    next.getUser().getName(),
    next.getCabin().getName(),
    next.getRequestedStartDate(),
    next.getRequestedEndDate(),
    token,
    expires
);
```

---

### **3. 📢 Notificaciones Generales del Admin**

**Ubicación:** **NO IMPLEMENTADO**

**Estado:** ❌ **NO IMPLEMENTADO**

**Endpoints pendientes:**

- `POST /api/admin/notifications/send` - Enviar notificaciones generales
- `GET /api/admin/notifications/history` - Historial de notificaciones

**Requerimientos para implementación:**

1. **Crear NotificationController** para endpoints admin
2. **Crear NotificationService** para envío masivo
3. **Crear NotificationHistory** entity para auditoría
4. **Implementar templates** para diferentes tipos de notificación
5. **Configurar destinatarios** (todos los usuarios, por rol, etc.)
6. **Agregar validaciones** de contenido y destinatarios

---

## 🏗️ **Arquitectura Sugerida**

### **NotificationService Interface**

```java
public interface NotificationService {
    void sendPasswordResetEmail(String email, String token, String documentNumber);
    void sendWaitingListNotification(String email, String userName, String cabinName,
                                   LocalDate startDate, LocalDate endDate, String token, LocalDateTime expires);
    void sendGeneralNotification(String subject, String content, List<String> recipients);
    void sendSMS(String phoneNumber, String message);
}
```

### **NotificationProvider Interface**

```java
public interface NotificationProvider {
    void sendEmail(String to, String subject, String content);
    void sendSMS(String to, String message);
}
```

### **Implementaciones Sugeridas**

- **EmailProvider**: SendGrid, AWS SES, Gmail SMTP
- **SMSProvider**: Twilio, AWS SNS
- **TemplateEngine**: Thymeleaf, FreeMarker

---

## ⚙️ **Configuración Requerida**

### **Variables de Entorno**

```yaml
# Email Configuration
notification:
  email:
    provider: sendgrid # sendgrid, aws-ses, gmail
    api-key: ${EMAIL_API_KEY}
    from-address: noreply@cooperativa.com
    templates-path: /templates/email/

  sms:
    provider: twilio # twilio, aws-sns
    account-sid: ${SMS_ACCOUNT_SID}
    auth-token: ${SMS_AUTH_TOKEN}
    from-number: ${SMS_FROM_NUMBER}

  queue:
    enabled: true
    max-retries: 3
    retry-delay: 5000
```

---

## 📊 **Priorización**

### **🚨 Prioridad Alta**

1. **Password Reset Email** - Funcionalidad crítica para recuperación de acceso
2. **Waiting List Notifications** - Funcionalidad core del sistema de reservas

### **📢 Prioridad Media**

3. **Notificaciones Generales del Admin** - Funcionalidad administrativa opcional

### **📱 Prioridad Baja**

4. **SMS Notifications** - Funcionalidad adicional opcional

---

## 🧪 **Testing**

### **Tests Requeridos**

- **Unit Tests**: NotificationService, EmailProvider, SMSProvider
- **Integration Tests**: End-to-end email sending
- **Mock Tests**: Para desarrollo sin proveedores reales

### **Test Configuration**

```yaml
# Test Profile
spring:
  profiles:
    active: test
notification:
  email:
    provider: mock
  sms:
    provider: mock
```

---

## 📈 **Métricas y Monitoreo**

### **Métricas a Implementar**

- `notification.email.sent` - Emails enviados exitosamente
- `notification.email.failed` - Emails fallidos
- `notification.sms.sent` - SMS enviados exitosamente
- `notification.sms.failed` - SMS fallidos
- `notification.queue.size` - Tamaño de cola de notificaciones

---

## 🔒 **Consideraciones de Seguridad**

### **Protección de Datos**

- **No loggear tokens** en logs de aplicación
- **Encriptar credenciales** de proveedores
- **Validar destinatarios** antes del envío
- **Rate limiting** para prevenir spam

### **Compliance**

- **GDPR**: Consentimiento para notificaciones
- **CAN-SPAM**: Incluir opción de unsubscribe
- **Auditoría**: Logs de todas las notificaciones enviadas

---

## 📅 **Cronograma Sugerido**

### **Fase 1: Email Básico (1-2 semanas)**

- Implementar NotificationService básico
- Integrar con SendGrid o AWS SES
- Implementar password reset emails
- Tests unitarios e integración

### **Fase 2: Waiting List (1 semana)**

- Implementar notificaciones de waiting list
- Templates de email personalizados
- Tests end-to-end

### **Fase 3: Notificaciones Generales (1-2 semanas)**

- Endpoints admin para notificaciones
- Sistema de templates
- Historial de notificaciones

### **Fase 4: SMS y Optimizaciones (1 semana)**

- Integración con Twilio
- Cola de notificaciones asíncronas
- Métricas y monitoreo

---

## ✅ **Criterios de Aceptación**

### **Password Reset Email**

- [ ] Email se envía correctamente al usuario
- [ ] Token funciona para reset de contraseña
- [ ] Manejo de errores de envío
- [ ] Logs de auditoría completos

### **Waiting List Notifications**

- [ ] Email se envía al siguiente en lista
- [ ] Información completa de cabaña y fechas
- [ ] Link funcional para reclamar reserva
- [ ] Expiración automática de tokens

### **Notificaciones Generales**

- [ ] Admin puede enviar notificaciones masivas
- [ ] Historial completo de notificaciones
- [ ] Filtros por destinatarios
- [ ] Templates personalizables

---

**Documento creado:** $(date)
**Estado:** Pendiente de implementación
**Prioridad:** Alta para emails, Media para notificaciones generales
