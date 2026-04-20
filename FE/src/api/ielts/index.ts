/**
 * IELTS API Module
 *
 * Central export for all IELTS-related API functions
 * Matches BE Controllers
 *
 * @module api/ielts
 * @author DOIT IELTS Team
 */

// Skill-specific APIs
export * from './listening'
export * from './reading'
export * from './writing'
export * from './speaking'

// Test management
export * from './attempts'
export * from './mock-test'
export * from './placement-test'

// User features
export * from './study-plan'
export * from './dashboard'

// Resources & Public
export * from './vocabulary'
export * from './resources'
export * from './grading'
export * from './public'
